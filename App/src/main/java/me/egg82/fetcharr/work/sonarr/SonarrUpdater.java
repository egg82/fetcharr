package me.egg82.fetcharr.work.sonarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.sonarr.SonarrV3API;
import me.egg82.arr.sonarr.v3.Episode;
import me.egg82.arr.sonarr.v3.Series;
import me.egg82.arr.sonarr.v3.Tag;
import me.egg82.arr.sonarr.v3.schema.EpisodeFileResource;
import me.egg82.arr.sonarr.v3.schema.EpisodeResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.arr.sonarr.v3.schema.TagResource;
import me.egg82.arr.unit.TimeValue;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.SonarrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SonarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedSeries> random = new WeightedRandom<>();

    public SonarrUpdater(@NotNull SonarrV3API api) {
        super(api);

        if (!Instant.EPOCH.equals(this.metaFile.lastUpdate())) {
            logger.debug("Resuming SONARR_{} from last update at {}", api.id(), this.metaFile.lastUpdate());
        }
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = SonarrConfigVars.getTimeValue(SonarrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = SonarrConfigVars.getInt(SonarrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for SONARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        Series allSeries = api.fetch(Series.class);
        if (allSeries == null) {
            logger.error("SONARR_{} returned bad result for {}", api.id(), Series.UNKNOWN.apiPath());
            return;
        }

        List<WeightedSeries> wrapped = new ArrayList<>();
        for (SeriesResource s : allSeries.resources()) {
            Episode allEpisodes = api.fetch(Episode.class, Map.of("seriesId", s.id()));
            if (allEpisodes == null) {
                logger.error("SONARR_{} returned bad result for {}", api.id(), Episode.UNKNOWN.apiPath());
                continue;
            }

            wrapped.add(new WeightedSeries(s, allEpisodes.resources()));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = SonarrConfigVars.getBool(SonarrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = SonarrConfigVars.getBool(SonarrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = SonarrConfigVars.getBool(SonarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = SonarrConfigVars.getArr(SonarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = ConfigVars.getBool(ConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            WeightedSeries s = random.selectOne();
            if (s == null) {
                continue;
            }
            if (monitoredOnly && !s.series().monitored()) {
                logger.info("Skipping series {} (\"{}\") due to unmonitored status", s.series().id(), s.series().title());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, s.series().tags())) {
                logger.info("Skipping series {} (\"{}\") because skip-tag is set", s.series().id(), s.series().title());
                continue;
            }
            if (missingOnly) {
                boolean hasFiles = true;
                for (EpisodeResource e : s.episodes()) {
                    if (!e.hasFile()) {
                        hasFiles = false;
                        break;
                    }
                }
                if (hasFiles) {
                    logger.info("Skipping series {} (\"{}\") because it is not missing any series files", s.series().id(), s.series().title());
                    continue;
                }
            }
            if (useCutoff) {
                boolean cutoffMet = true;
                for (EpisodeResource e : s.episodes()) {
                    EpisodeFileResource episodeFile = e.episodeFile();
                    if (episodeFile != null && !episodeFile.qualityCutoffNotMet()) {
                        cutoffMet = false;
                        break;
                    }
                }
                if (cutoffMet) {
                    logger.info("Skipping series {} (\"{}\") because it meets the quality cutoff", s.series().id(), s.series().title());
                    continue;
                }
            }

            if (dryRun) {
                logger.info("Would update series {} (\"{}\") if not in dry-run mode", s.series().id(), s.series().title());
            } else {
                logger.info("Updating series {} (\"{}\")", s.series().id(), s.series().title());
            }
            ids.add(s.series().id());
            api.invalidate(Series.class, s.series().id()); // Force refresh on next
            api.invalidate(Episode.class, Map.of("seriesId", s.series().id())); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.metaFile.lastUpdate(lastUpdate);
        this.metaFile.write();
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
