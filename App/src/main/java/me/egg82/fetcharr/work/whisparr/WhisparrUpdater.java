package me.egg82.fetcharr.work.whisparr;

import me.egg82.arr.whisparr.WhisparrV3API;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

public class WhisparrUpdater extends AbstractUpdater {
    public WhisparrUpdater(@NotNull WhisparrV3API api) {
        super(api);
    }

    @Override
    protected void doWork() {

    }

    /*private final WeightedRandom<Movie> random = new WeightedRandom<>();

    public WhisparrUpdater(@NotNull WhisparrAPI api) {
        super(api);
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = WhisparrConfigVars.getTimeValue(WhisparrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = WhisparrConfigVars.getInt(WhisparrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for WHISPARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        AllMovies all = api.fetch(AllMovies.class, false);
        random.updateList(all.items());

        boolean monitoredOnly = WhisparrConfigVars.getBool(WhisparrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = WhisparrConfigVars.getBool(WhisparrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = WhisparrConfigVars.getBool(WhisparrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = WhisparrConfigVars.getArr(WhisparrConfigVars.SKIP_TAGS, api.id());

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
                logger.info("Skipping scene/movie {} (\"{}\") due to unmonitored status", m.id(), m.title());
                continue;
            }
            if (missingOnly && m.hasFile()) {
                logger.info("Skipping scene/movie {} (\"{}\") because it is not missing a scene/movie file", m.id(), m.title());
                continue;
            }
            if (useCutoff && !m.movieFile().qualityCutoffNotMet()) {
                logger.info("Skipping scene/movie {} (\"{}\") because it meets the quality cutoff", m.id(), m.title());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, m.tags())) {
                logger.info("Skipping scene/movie {} (\"{}\") because skip-tag is set", m.id(), m.title());
                continue;
            }

            if (dryRun) {
                logger.info("Would update scene/movie {} (\"{}\") if not in dry-run mode", m.id(), m.title());
            } else {
                logger.info("Updating scene/movie {} (\"{}\")", m.id(), m.title());
            }
            ids.add(m.id());
            m.invalidate(); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.meta.last(lastUpdate);
        this.meta.write();
    }*/
}
