package me.egg82.fetcharr.work.lidarr;

import me.egg82.arr.lidarr.LidarrV1API;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

public class LidarrUpdater extends AbstractUpdater {
    public LidarrUpdater(@NotNull LidarrV1API api) {
        super(api);
    }

    @Override
    protected void doWork() {

    }

    /*private final WeightedRandom<Artist> random = new WeightedRandom<>();

    public LidarrUpdater(@NotNull LidarrAPI api) {
        super(api);
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = LidarrConfigVars.getTimeValue(LidarrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = LidarrConfigVars.getInt(LidarrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for LIDARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        AllArtists all = api.fetch(AllArtists.class, false);
        random.updateList(all.items());

        boolean monitoredOnly = LidarrConfigVars.getBool(LidarrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = LidarrConfigVars.getBool(LidarrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = LidarrConfigVars.getBool(LidarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = LidarrConfigVars.getArr(LidarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = ConfigVars.getBool(ConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            Artist a = random.selectOne();
            if (a == null) {
                continue;
            }
            api.update(a);
            if (monitoredOnly && !a.monitored()) {
                logger.info("Skipping artist {} (\"{}\") due to unmonitored status", a.id(), a.artistName());
                continue;
            }
            if (missingOnly) {
                boolean missing = true;
                AllTracks at = api.fetch(AllTracks.class, a.id(), false);
                for (Track t : at.items()) {
                    if (!t.hasFile()) {
                        missing = false;
                        break;
                    }
                }
                if (!missing) {
                    logger.info("Skipping artist {} (\"{}\") because it has all available tracks", a.id(), a.artistName());
                    continue;
                }
            }
            if (useCutoff) {
                boolean qualityCutoffMet = true;
                AllTracks at = api.fetch(AllTracks.class, a.id(), false);
                for (Track t : at.items()) {
                    if (!t.trackFile().qualityCutoffNotMet()) {
                        qualityCutoffMet = false;
                        break;
                    }
                }
                if (qualityCutoffMet) {
                    logger.info("Skipping artist {} (\"{}\") because it meets the quality cutoff", a.id(), a.artistName());
                    continue;
                }
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, a.tags())) {
                logger.info("Skipping artist {} (\"{}\") because skip-tag is set", a.id(), a.artistName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update artist {} (\"{}\") if not in dry-run mode", a.id(), a.artistName());
            } else {
                logger.info("Updating artist {} (\"{}\")", a.id(), a.artistName());
            }
            ids.add(a.id());
            a.invalidate(); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.meta.last(lastUpdate);
        this.meta.write();
    }*/
}
