package me.egg82.fetcharr.work.radarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.web.model.radarr.AllMovies;
import me.egg82.fetcharr.web.model.radarr.Movie;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class RadarrUpdater extends AbstractUpdater {
    private final WeightedRandom<Movie> random = new WeightedRandom<>();

    public RadarrUpdater(@NotNull RadarrAPI api) {
        super(api);
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = RadarrConfigVars.getTimeValue(RadarrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = RadarrConfigVars.getInt(RadarrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for RADARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        AllMovies all = api.fetch(AllMovies.class, false);
        random.updateList(all.items());

        boolean monitoredOnly = RadarrConfigVars.getBool(RadarrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = RadarrConfigVars.getBool(RadarrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = RadarrConfigVars.getBool(RadarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = RadarrConfigVars.getArr(RadarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = ConfigVars.getBool(ConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            Movie m = random.selectOne();
            if (m == null) {
                continue;
            }
            api.update(m);
            if (monitoredOnly && !m.monitored()) {
                logger.info("Skipping movie {} (\"{}\") due to unmonitored status", m.id(), m.title());
                continue;
            }
            if (missingOnly && m.hasFile()) {
                logger.info("Skipping movie {} (\"{}\") because it is not missing a movie file", m.id(), m.title());
                continue;
            }
            if (useCutoff && !m.movieFile().qualityCutoffNotMet()) {
                logger.info("Skipping movie {} (\"{}\") because it meets the quality cutoff", m.id(), m.title());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, m.tags())) {
                logger.info("Skipping movie {} (\"{}\") because skip-tag is set", m.id(), m.title());
                continue;
            }

            if (dryRun) {
                logger.info("Would update movie {} (\"{}\") if not in dry-run mode", m.id(), m.title());
            } else {
                logger.info("Updating movie {} (\"{}\")", m.id(), m.title());
            }
            ids.add(m.id());
            m.invalidate(); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.meta.last(lastUpdate);
        this.meta.write();
    }
}
