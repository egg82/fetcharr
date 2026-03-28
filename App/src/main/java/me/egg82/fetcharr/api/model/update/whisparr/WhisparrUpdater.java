package me.egg82.fetcharr.api.model.update.whisparr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.whisparr.WhisparrV3API;
import me.egg82.arr.whisparr.v3.Movie;
import me.egg82.arr.whisparr.v3.Tag;
import me.egg82.arr.whisparr.v3.schema.MovieFileResource;
import me.egg82.arr.whisparr.v3.schema.MovieResource;
import me.egg82.arr.whisparr.v3.schema.TagResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.whisparr.*;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.MissingStatus;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhisparrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedMovie> random = new WeightedRandom<>();

    public WhisparrUpdater(@NotNull FetcharrAPI api, @NotNull WhisparrV3API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.WHISPARR, id));
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
        logger.debug("Fetched {} scenes/movies", all.resources().size());
        api.bus().post(new WhisparrFetchMovieEvent(all, this, api));

        List<WeightedMovie> wrapped = new ArrayList<>();
        for (MovieResource m : all.resources()) {
            wrapped.add(new WeightedMovie(m));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = config.monitoredOnly();
        MissingStatus missingStatus = config.missingStatus();
        if (missingStatus == MissingStatus.ALL && config.missingOnly()) { // TODO: Temp - remove in a future version
            missingStatus = MissingStatus.MISSING;
        }
        boolean useCutoff = config.useCutoff();
        PSet<@NotNull String> skipTags = config.skipTags();

        boolean dryRun = api.updateManager().dryRun();

        List<MovieResource> resources = new ArrayList<>();
        int attempts = 100;
        while (attempts > 0 && resources.size() < searchAmount) {
            attempts--;

            WeightedMovie m = random.selectOne();
            if (m == null) {
                continue;
            }

            WhisparrSelectMovieEvent selectMovieEvent = new WhisparrSelectMovieEvent(m.resource(), this, api);
            api.bus().post(selectMovieEvent);
            if (selectMovieEvent.cancelled()) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.PLUGIN, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with scene/movie {} (\"{}\")", selectMovieEvent.getClass().getSimpleName(), skipMovieSelectionEvent.getClass().getSimpleName(), m.resource().id(), m.resource().title());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") due to {} cancellation", m.resource().id(), m.resource().title(), selectMovieEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !m.resource().monitored()) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.UNMONITORED, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Unmonitored scene/movie {} (\"{}\"), but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") due to unmonitored status", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            if (missingStatus == MissingStatus.MISSING && m.resource().hasFile()) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.NOT_MISSING, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Scene/movie {} (\"{}\") not missing a movie file (missing only), but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") because it is not missing a movie file (missing only)", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            if (missingStatus == MissingStatus.UPGRADE && m.resource().hasFile()) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.MISSING, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Scene/movie {} (\"{}\") missing a movie file (upgrade only), but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") because it is missing a movie file (upgrade only)", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            MovieFileResource movieFile = m.resource().movieFile();
            if (useCutoff && movieFile != null && !movieFile.qualityCutoffNotMet()) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.QUALITY_CUTOFF_MET, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Scene/movie {} (\"{}\") quality cutoff met, but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") because it meets the quality cutoff", m.resource().id(), m.resource().title());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, m.resource().tags())) {
                WhisparrSkipMovieSelectionEvent skipMovieSelectionEvent = new WhisparrSkipMovieSelectionEvent(m.resource(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                api.bus().post(skipMovieSelectionEvent);
                if (skipMovieSelectionEvent.cancelled()) {
                    logger.info("Scene/ovie {} (\"{}\") has skip-tag set, but {} cancelled - continuing", m.resource().id(), m.resource().title(), skipMovieSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping scene/movie {} (\"{}\") because skip-tag is set", m.resource().id(), m.resource().title());
                    continue;
                }
            }

            WhisparrUpdateMovieEvent updateMovieEvent = new WhisparrUpdateMovieEvent(m.resource(), this, api);
            api.bus().post(updateMovieEvent);
            if (updateMovieEvent.cancelled()) {
                logger.info("Skipping scene/movie {} (\"{}\") due to {} cancellation", m.resource().id(), m.resource().title(), updateMovieEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update scene/movie {} (\"{}\") if not in dry-run mode", m.resource().id(), m.resource().title());
            } else {
                logger.info("Updating scene/movie {} (\"{}\")", m.resource().id(), m.resource().title());
            }
            resources.add(m.resource());
            arrApi.invalidate(Movie.class, m.resource().id()); // Force refresh on next
        }

        if (!dryRun && !resources.isEmpty()) {
            WhisparrSearchEvent searchEvent = new WhisparrSearchEvent(resources, this, api);
            api.bus().post(searchEvent);
            if (!searchEvent.cancelled()) {
                IntList ids = new IntArrayList();
                for (MovieResource r : searchEvent.resources()) {
                    ids.add(r.id());
                }
                arrApi.search(ids);
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
