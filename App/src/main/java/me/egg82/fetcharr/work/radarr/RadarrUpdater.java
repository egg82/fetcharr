package me.egg82.fetcharr.work.radarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.config.Tristate;
import me.egg82.arr.radarr.RadarrV3API;
import me.egg82.arr.radarr.v3.Movie;
import me.egg82.arr.radarr.v3.Tag;
import me.egg82.arr.radarr.v3.schema.MovieFileResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.arr.radarr.v3.schema.TagResource;
import me.egg82.arr.unit.TimeValue;
import me.egg82.fetcharr.env.CommonConfigVars;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RadarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedMovie> random = new WeightedRandom<>();

    public RadarrUpdater(@NotNull RadarrV3API api) {
        super(api);

        if (!Instant.EPOCH.equals(this.metaFile.lastUpdate())) {
            logger.debug("Resuming RADARR_{} from last update at {}", api.id(), this.metaFile.lastUpdate());
        }
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
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for RADARR_{}: {}", searchAmount, api.id(), api.baseUrl());
            return;
        }

        logger.info("Updating up to {} items for for RADARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        Movie all = api.fetch(Movie.class);
        if (all == null) {
            logger.error("RADARR_{} returned bad result for {}", api.id(), Movie.UNKNOWN.apiPath());
            return;
        }

        List<WeightedMovie> wrapped = new ArrayList<>();
        for (MovieResource m : all.resources()) {
            wrapped.add(new WeightedMovie(m));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = RadarrConfigVars.getBool(RadarrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = RadarrConfigVars.getBool(RadarrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = RadarrConfigVars.getBool(RadarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = RadarrConfigVars.getArr(RadarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = CommonConfigVars.getBool(CommonConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            WeightedMovie m = random.selectOne();
            if (m == null) {
                continue;
            }
            if (monitoredOnly && !m.resource().monitored()) {
                logger.info("Skipping movie {} (\"{}\") due to unmonitored status", m.resource().id(), m.resource().title());
                continue;
            }
            if (missingOnly && m.resource().hasFile()) {
                logger.info("Skipping movie {} (\"{}\") because it is not missing a movie file", m.resource().id(), m.resource().title());
                continue;
            }
            MovieFileResource movieFile = m.resource().movieFile();
            if (useCutoff && movieFile != null && !movieFile.qualityCutoffNotMet()) {
                logger.info("Skipping movie {} (\"{}\") because it meets the quality cutoff", m.resource().id(), m.resource().title());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, m.resource().tags())) {
                logger.info("Skipping movie {} (\"{}\") because skip-tag is set", m.resource().id(), m.resource().title());
                continue;
            }

            if (dryRun) {
                logger.info("Would update movie {} (\"{}\") if not in dry-run mode", m.resource().id(), m.resource().title());
            } else {
                logger.info("Updating movie {} (\"{}\")", m.resource().id(), m.resource().title());
            }
            ids.add(m.resource().id());
            api.invalidate(Movie.class, m.resource().id()); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.metaFile.lastUpdate(lastUpdate);
        Tristate fileCache = CacheConfigVars.getTristate(CacheConfigVars.USE_FILE_CACHE);
        if ((fileCache == Tristate.AUTO && isCacheWritable()) || fileCache == Tristate.TRUE) {
            this.metaFile.write();
        }
    }

    private boolean hasAnyTag(@NotNull String @NotNull [] needles, @NotNull Collection<@NotNull Tag> haystack) {
        if (needles.length == 0 || haystack.isEmpty()) {
            return false;
        }

        for (String n : needles) {
            for (Tag t : haystack) {
                TagResource r = t.resource();
                if (r != null) {
                    String label = r.label();
                    if (label != null && label.equalsIgnoreCase(n)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
