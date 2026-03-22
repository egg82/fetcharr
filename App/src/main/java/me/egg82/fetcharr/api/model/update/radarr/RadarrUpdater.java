package me.egg82.fetcharr.api.model.update.radarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.radarr.RadarrV3API;
import me.egg82.arr.radarr.v3.Movie;
import me.egg82.arr.radarr.v3.Tag;
import me.egg82.arr.radarr.v3.schema.MovieFileResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.arr.radarr.v3.schema.TagResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.APISearchEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.radarr.*;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RadarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedMovie> random = new WeightedRandom<>();

    public RadarrUpdater(@NotNull FetcharrAPI api, @NotNull RadarrV3API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.RADARR, id));
    }

    @Override
    protected boolean doWork() {
        int searchAmount = config.searchAmount();
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());
            return true; // Bad config, no need to retry every run
        }

        logger.info("Updating up to {} items for for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());

        Movie all = arrApi.fetch(Movie.class);
        if (all == null) {
            logger.error("{}_{} returned bad result for {}", config.type().name(), config.id(), Movie.UNKNOWN.apiPath());
            return false;
        }
        logger.debug("Fetched {} movies", all.resources().size());
        api.bus().post(new RadarrFetchMovieEvent(all, this, api));

        List<WeightedMovie> wrapped = new ArrayList<>();
        for (MovieResource m : all.resources()) {
            wrapped.add(new WeightedMovie(m));
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

            WeightedMovie m = random.selectOne();
            if (m == null) {
                continue;
            }

            RadarrSelectMovieEvent selectMovieEvent = new RadarrSelectMovieEvent(m.resource(), this, api);
            api.bus().post(selectMovieEvent);
            if (selectMovieEvent.cancelled()) {
                RadarrSkipMovieSelectionEvent skipMovieSelectionEvent = new RadarrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.PLUGIN, this, api);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with movie {} (\"{}\")", selectMovieEvent.getClass().getSimpleName(), skipMovieSelectionEvent.getClass().getSimpleName(), m.resource().id(), m.resource().title());
                } else {
                    logger.info("Skipping movie {} (\"{}\") due to {} cancellation", m.resource().id(), m.resource().title(), selectMovieEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !m.resource().monitored()) {
                RadarrSkipMovieSelectionEvent skipMovieSelectionEvent = new RadarrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.UNMONITORED, this, api);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Unmonitored movie {} (\"{}\"), but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping movie {} (\"{}\") due to unmonitored status", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            if (missingOnly && m.resource().hasFile()) {
                RadarrSkipMovieSelectionEvent skipMovieSelectionEvent = new RadarrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.NOT_MISSING, this, api);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Movie {} (\"{}\") not missing a movie file, but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping movie {} (\"{}\") because it is not missing a movie file", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            MovieFileResource movieFile = m.resource().movieFile();
            if (useCutoff && movieFile != null && !movieFile.qualityCutoffNotMet()) {
                RadarrSkipMovieSelectionEvent skipMovieSelectionEvent = new RadarrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.QUALITY_CUTOFF_MET, this, api);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Movie {} (\"{}\") quality cutoff met, but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping movie {} (\"{}\") because it meets the quality cutoff", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, m.resource().tags())) {
                RadarrSkipMovieSelectionEvent skipMovieSelectionEvent = new RadarrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Movie {} (\"{}\") has skip-tag set, but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping movie {} (\"{}\") because skip-tag is set", m.resource().id(), m.resource().title());
                    continue;
                }
            }

            RadarrUpdateMovieEvent updateMovieEvent = new RadarrUpdateMovieEvent(m.resource(), this, api);
            if (updateMovieEvent.cancelled()) {
                logger.info("Skipping movie {} (\"{}\") due to {} cancellation", m.resource().id(), m.resource().title(), updateMovieEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update movie {} (\"{}\") if not in dry-run mode", m.resource().id(), m.resource().title());
            } else {
                logger.info("Updating movie {} (\"{}\")", m.resource().id(), m.resource().title());
            }
            ids.add(m.resource().id());
            arrApi.invalidate(Movie.class, m.resource().id()); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            APISearchEvent searchEvent = new APISearchEvent(ids, this, api);
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
