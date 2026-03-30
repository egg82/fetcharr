package me.egg82.fetcharr.api.model.update.readarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.readarr.ReadarrV1API;
import me.egg82.arr.readarr.v1.Author;
import me.egg82.arr.readarr.v1.AuthorSearchCommand;
import me.egg82.arr.readarr.v1.Book;
import me.egg82.arr.readarr.v1.Tag;
import me.egg82.arr.readarr.v1.model.AuthorResource;
import me.egg82.arr.readarr.v1.model.CommandResource;
import me.egg82.arr.readarr.v1.model.TagResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.MissingStatus;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReadarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedAuthor> random = new WeightedRandom<>();

    public ReadarrUpdater(@NotNull FetcharrAPI api, @NotNull ReadarrV1API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.READARR, id));
    }

    @Override
    protected boolean doWork() {
        int searchAmount = config.searchAmount();
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());
            return true;
        }

        logger.info("Updating up to {} items for for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());

        Author allAuthors = arrApi.fetch(Author.class);
        if (allAuthors == null) {
            logger.error("{}_{} returned bad result for {}", config.type().name(), config.id(), Author.UNKNOWN.apiPath());
            return false; // Bad config, no need to retry every run
        }
        logger.debug("Fetched {} authors", allAuthors.resources().size());
        api.bus().post(new LidarrFetchArtistEvent(allAuthors, this, api));

        List<WeightedAuthor> wrapped = new ArrayList<>();
        for (AuthorResource a : allAuthors.resources()) {
            Book allBooks = arrApi.fetch(Book.class, Map.of("authorId", a.id()));
            if (allBooks == null) {
                logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Book.UNKNOWN.apiPath());
                continue;
            }
            logger.debug("Fetched {} albums for author {} (\"{}\")", allBooks.resources().size(), a.id(), a.authorName());
            api.bus().post(new LidarrFetchAlbumsEvent(allBooks, a, this, api));
            wrapped.add(new WeightedAuthor(a, allBooks.resources()));
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

        List<AuthorResource> resources = new ArrayList<>();
        int attempts = 100;
        while (attempts > 0 && resources.size() < searchAmount) {
            attempts--;

            WeightedAuthor a = random.selectOne();
            if (a == null) {
                continue;
            }

            LidarrSelectArtistEvent selectAuthorEvent = new LidarrSelectArtistEvent(a.author(), this, api);
            api.bus().post(selectAuthorEvent);
            if (selectAuthorEvent.cancelled()) {
                LidarrSkipArtistSelectionEvent skipAuthorSelectionEvent = new LidarrSkipArtistSelectionEvent(a.author(), SelectionCancellationReason.PLUGIN, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with author {} (\"{}\")", skipAuthorSelectionEvent.getClass().getSimpleName(), skipAuthorSelectionEvent.getClass().getSimpleName(), a.author().id(), a.author().authorName());
                } else {
                    logger.info("Skipping author {} (\"{}\") due to {} cancellation", a.author().id(), a.author().authorName(), skipAuthorSelectionEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !a.author().monitored()) {
                LidarrSkipArtistSelectionEvent skipAuthorSelectionEvent = new LidarrSkipArtistSelectionEvent(a.author(), SelectionCancellationReason.UNMONITORED, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("Unmonitored author {} (\"{}\"), but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping author {} (\"{}\") due to unmonitored status", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, a.author().tags())) {
                LidarrSkipArtistSelectionEvent skipAuthorSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                api.bus().post(skipAuthorSelectionEvent);
                if (skipAuthorSelectionEvent.cancelled()) {
                    logger.info("Author {} (\"{}\") has skip-tag set, but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping author {} (\"{}\") because skip-tag is set", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }
            if (missingStatus == MissingStatus.MISSING || missingStatus == MissingStatus.UPGRADE) {
                Track allTracks = arrApi.fetch(Track.class, Map.of("artistId", a.artist().id()));
                if (allTracks == null) {
                    logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Track.UNKNOWN.apiPath());
                    continue;
                }
                logger.debug("Fetched {} tracks for artist {} (\"{}\")", allTracks.resources().size(), a.artist().id(), a.artist().artistName());
                api.bus().post(new LidarrFetchTracksEvent(allTracks, a.artist(), this, api));

                boolean hasFiles = true;
                for (TrackResource t : allTracks.resources()) {
                    if (!t.hasFile()) {
                        hasFiles = false;
                        break;
                    }
                }
                if (missingStatus == MissingStatus.MISSING && hasFiles) {
                    LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.NOT_MISSING, this, api);
                    api.bus().post(skipArtistSelectionEvent);
                    if (skipArtistSelectionEvent.cancelled()) {
                        logger.info("Artist {} (\"{}\") not missing any track files (missing only), but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping artist {} (\"{}\") because it is not missing any track files (missing only)", a.artist().id(), a.artist().artistName());
                        continue;
                    }
                }
                if (missingStatus == MissingStatus.UPGRADE && !hasFiles) {
                    LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.MISSING, this, api);
                    api.bus().post(skipArtistSelectionEvent);
                    if (skipArtistSelectionEvent.cancelled()) {
                        logger.info("Artist {} (\"{}\") not missing any track files (upgrade only), but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping artist {} (\"{}\") because it is not missing any track files (upgrade only)", a.artist().id(), a.artist().artistName());
                        continue;
                    }
                }
            }
            if (useCutoff) {
                Track allTracks = arrApi.fetch(Track.class, Map.of("artistId", a.artist().id()));
                if (allTracks == null) {
                    logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Track.UNKNOWN.apiPath());
                    continue;
                }
                logger.debug("Fetched {} tracks for artist {} (\"{}\")", allTracks.resources().size(), a.artist().id(), a.artist().artistName());
                api.bus().post(new LidarrFetchTracksEvent(allTracks, a.artist(), this, api));

                boolean cutoffMet = true;
                for (TrackResource t : allTracks.resources()) {
                    TrackFileResource trackFile = t.trackFile();
                    if (trackFile != null && !trackFile.qualityCutoffNotMet()) {
                        cutoffMet = false;
                        break;
                    }
                }
                if (cutoffMet) {
                    LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.QUALITY_CUTOFF_MET, this, api);
                    api.bus().post(skipArtistSelectionEvent);
                    if (skipArtistSelectionEvent.cancelled()) {
                        logger.info("Artist {} (\"{}\") quality cutoff met, but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping artist {} (\"{}\") because it meets the quality cutoff", a.artist().id(), a.artist().artistName());
                        continue;
                    }
                }
            }

            LidarrUpdateArtistEvent updateArtistEvent = new LidarrUpdateArtistEvent(a.artist(), this, api);
            api.bus().post(updateArtistEvent);
            if (updateArtistEvent.cancelled()) {
                logger.info("Skipping artist {} (\"{}\") due to {} cancellation", a.artist().id(), a.artist().artistName(), updateArtistEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update artist {} (\"{}\") if not in dry-run mode", a.artist().id(), a.artist().artistName());
            } else {
                logger.info("Updating artist {} (\"{}\")", a.artist().id(), a.artist().artistName());
            }
            resources.add(a.artist());
            arrApi.invalidate(Artist.class, a.artist().id()); // Force refresh on next
            arrApi.invalidate(Track.class, Map.of("artistId", a.artist().id())); // Force refresh on next
        }

        if (!dryRun && !resources.isEmpty()) {
            LidarrPreSearchEvent preSearchEvent = new LidarrPreSearchEvent(resources, this, api);
            api.bus().post(preSearchEvent);
            if (!preSearchEvent.cancelled()) {
                IntList ids = new IntArrayList();
                for (ArtistResource r : preSearchEvent.resources()) {
                    ids.add(r.id());
                }
                CommandResource result = arrApi.send(new AuthorSearchCommand(ids.toIntArray()), CommandResource.class);
                ReadarrPostSearchEvent postSearchEvent = new ReadarrPostSearchEvent(result, this, api);
            } else {
                logger.info("{} cancelled - not performing search for {}_{}", preSearchEvent.getClass().getSimpleName(), config.type().name(), config.id());
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
