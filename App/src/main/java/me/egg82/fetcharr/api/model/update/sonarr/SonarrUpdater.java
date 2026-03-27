package me.egg82.fetcharr.api.model.update.sonarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.sonarr.SonarrV3API;
import me.egg82.arr.sonarr.v3.Episode;
import me.egg82.arr.sonarr.v3.Series;
import me.egg82.arr.sonarr.v3.Tag;
import me.egg82.arr.sonarr.v3.schema.EpisodeFileResource;
import me.egg82.arr.sonarr.v3.schema.EpisodeResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.arr.sonarr.v3.schema.TagResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.APISearchEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.sonarr.*;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SonarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedSeries> random = new WeightedRandom<>();

    public SonarrUpdater(@NotNull FetcharrAPI api, @NotNull SonarrV3API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.SONARR, id));
    }

    @Override
    protected boolean doWork() {
        int searchAmount = config.searchAmount();
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());
            return true;
        }

        logger.info("Updating up to {} items for for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());

        Series allSeries = arrApi.fetch(Series.class);
        if (allSeries == null) {
            logger.error("{}_{} returned bad result for {}", config.type().name(), config.id(), Series.UNKNOWN.apiPath());
            return false; // Bad config, no need to retry every run
        }
        logger.debug("Fetched {} series", allSeries.resources().size());
        api.bus().post(new SonarrFetchSeriesEvent(allSeries, this, api));

        List<WeightedSeries> wrapped = new ArrayList<>();
        for (SeriesResource s : allSeries.resources()) {
            Episode allEpisodes = arrApi.fetch(Episode.class, Map.of("seriesId", s.id()));
            if (allEpisodes == null) {
                logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Episode.UNKNOWN.apiPath());
                continue;
            }
            logger.debug("Fetched {} episodes for series {} (\"{}\")", allEpisodes.resources().size(), s.id(), s.title());
            api.bus().post(new SonarrFetchEpisodesEvent(allEpisodes, s, this, api));
            wrapped.add(new WeightedSeries(s, allEpisodes.resources()));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = config.monitoredOnly();
        boolean missingOnly = config.missingOnly();
        boolean useCutoff = config.useCutoff();
        PSet<@NotNull String> skipTags = config.skipTags();

        boolean dryRun = api.updateManager().dryRun();

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            WeightedSeries s = random.selectOne();
            if (s == null) {
                continue;
            }

            SonarrSelectSeriesEvent selectSeriesEvent = new SonarrSelectSeriesEvent(s.series(), this, api);
            api.bus().post(selectSeriesEvent);
            if (selectSeriesEvent.cancelled()) {
                SonarrSkipSeriesSelectionEvent skipSeriesSelectionEvent = new SonarrSkipSeriesSelectionEvent(s.series(), SelectionCancellationReason.PLUGIN, this, api);
                api.bus().post(skipSeriesSelectionEvent);
                if (skipSeriesSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with series {} (\"{}\")", skipSeriesSelectionEvent.getClass().getSimpleName(), skipSeriesSelectionEvent.getClass().getSimpleName(), s.series().id(), s.series().title());
                } else {
                    logger.info("Skipping series {} (\"{}\") due to {} cancellation", s.series().id(), s.series().title(), skipSeriesSelectionEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !s.series().monitored()) {
                SonarrSkipSeriesSelectionEvent skipSeriesSelectionEvent = new SonarrSkipSeriesSelectionEvent(s.series(), SelectionCancellationReason.UNMONITORED, this, api);
                api.bus().post(skipSeriesSelectionEvent);
                if (skipSeriesSelectionEvent.cancelled()) {
                    logger.info("Unmonitored series {} (\"{}\"), but {} cancelled - continuing", s.series().id(), s.series().title(), skipSeriesSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping series {} (\"{}\") due to unmonitored status", s.series().id(), s.series().title());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, s.series().tags())) {
                SonarrSkipSeriesSelectionEvent skipSeriesSelectionEvent = new SonarrSkipSeriesSelectionEvent(s.series(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                api.bus().post(skipSeriesSelectionEvent);
                if (skipSeriesSelectionEvent.cancelled()) {
                    logger.info("Series {} (\"{}\") has skip-tag set, but {} cancelled - continuing", s.series().id(), s.series().title(), skipSeriesSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping series {} (\"{}\") because skip-tag is set", s.series().id(), s.series().title());
                    continue;
                }
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
                    SonarrSkipSeriesSelectionEvent skipSeriesSelectionEvent = new SonarrSkipSeriesSelectionEvent(s.series(), SelectionCancellationReason.NOT_MISSING, this, api);
                    api.bus().post(skipSeriesSelectionEvent);
                    if (skipSeriesSelectionEvent.cancelled()) {
                        logger.info("Series {} (\"{}\") not missing any episode files, but {} cancelled - continuing", s.series().id(), s.series().title(), skipSeriesSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping series {} (\"{}\") because it is not missing any episode files", s.series().id(), s.series().title());
                        continue;
                    }
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
                    SonarrSkipSeriesSelectionEvent skipSeriesSelectionEvent = new SonarrSkipSeriesSelectionEvent(s.series(), SelectionCancellationReason.QUALITY_CUTOFF_MET, this, api);
                    api.bus().post(skipSeriesSelectionEvent);
                    if (skipSeriesSelectionEvent.cancelled()) {
                        logger.info("Series {} (\"{}\") quality cutoff met, but {} cancelled - continuing", s.series().id(), s.series().title(), skipSeriesSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping series {} (\"{}\") because it meets the quality cutoff", s.series().id(), s.series().title());
                        continue;
                    }
                }
            }

            SonarrUpdateSeriesEvent updateSeriesEvent = new SonarrUpdateSeriesEvent(s.series(), this, api);
            api.bus().post(updateSeriesEvent);
            if (updateSeriesEvent.cancelled()) {
                logger.info("Skipping series {} (\"{}\") due to {} cancellation", s.series().id(), s.series().title(), updateSeriesEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update series {} (\"{}\") if not in dry-run mode", s.series().id(), s.series().title());
            } else {
                logger.info("Updating series {} (\"{}\")", s.series().id(), s.series().title());
            }
            ids.add(s.series().id());
            arrApi.invalidate(Series.class, s.series().id()); // Force refresh on next
            arrApi.invalidate(Episode.class, Map.of("seriesId", s.series().id())); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            APISearchEvent searchEvent = new APISearchEvent(ids, this, api);
            api.bus().post(searchEvent);
            if (!searchEvent.cancelled()) {
                arrApi.search(searchEvent.ids());
            } else {
                logger.info("{} cancelled - not performing search for {}_{}", searchEvent.getClass().getSimpleName(), config.type().name(), config.id());
            }
        }

        random.clear();
        return true;
    }

    private boolean hasAnyTag(@NotNull Collection<@NotNull String> needles, @NotNull Collection<@NotNull Tag> haystack) {
        if (needles.isEmpty() || haystack.isEmpty()) {
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
