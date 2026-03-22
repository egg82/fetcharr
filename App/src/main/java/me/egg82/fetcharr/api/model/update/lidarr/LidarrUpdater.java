package me.egg82.fetcharr.api.model.update.lidarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.lidarr.LidarrV1API;
import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.lidarr.v1.Artist;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.lidarr.v1.Track;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.lidarr.v1.schema.TagResource;
import me.egg82.arr.lidarr.v1.schema.TrackFileResource;
import me.egg82.arr.lidarr.v1.schema.TrackResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.APISearchEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.lidarr.*;
import me.egg82.fetcharr.api.model.update.AbstractUpdater;
import me.egg82.fetcharr.api.model.update.UpdaterConfigImpl;
import me.egg82.fetcharr.util.WeightedRandom;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LidarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedArtist> random = new WeightedRandom<>();

    public LidarrUpdater(@NotNull FetcharrAPI api, @NotNull LidarrV1API arrApi, int id) {
        super(api, arrApi, new UpdaterConfigImpl(ArrType.LIDARR, id));
    }

    @Override
    protected boolean doWork() {
        int searchAmount = config.searchAmount();
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());
            return true;
        }

        logger.info("Updating up to {} items for for {}_{}: {}", searchAmount, config.type().name(), config.id(), arrApi.baseUrl());

        Artist allArtists = arrApi.fetch(Artist.class);
        if (allArtists == null) {
            logger.error("{}_{} returned bad result for {}", config.type().name(), config.id(), Artist.UNKNOWN.apiPath());
            return false; // Bad config, no need to retry every run
        }
        logger.debug("Fetched {} artists", allArtists.resources().size());
        api.bus().post(new LidarrFetchArtistEvent(allArtists, this, api));

        List<WeightedArtist> wrapped = new ArrayList<>();
        for (ArtistResource a : allArtists.resources()) {
            Album allAlbums = arrApi.fetch(Album.class, Map.of("artistId", a.id()));
            if (allAlbums == null) {
                logger.warn("{}_{} returned bad result for {}", config.type().name(), config.id(), Album.UNKNOWN.apiPath());
                continue;
            }
            logger.debug("Fetched {} albums for artist {} (\"{}\")", allAlbums.resources().size(), a.id(), a.artistName());
            api.bus().post(new LidarrFetchAlbumsEvent(allAlbums, a, this, api));
            wrapped.add(new WeightedArtist(a, allAlbums.resources()));
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

            WeightedArtist a = random.selectOne();
            if (a == null) {
                continue;
            }

            LidarrSelectArtistEvent selectArtistEvent = new LidarrSelectArtistEvent(a.artist(), this, api);
            api.bus().post(selectArtistEvent);
            if (selectArtistEvent.cancelled()) {
                LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.PLUGIN, this, api);
                if (skipArtistSelectionEvent.cancelled()) {
                    logger.info("{} cancelled, but {} also cancelled - continuing with artist {} (\"{}\")", skipArtistSelectionEvent.getClass().getSimpleName(), skipArtistSelectionEvent.getClass().getSimpleName(), a.artist().id(), a.artist().artistName());
                } else {
                    logger.info("Skipping artist {} (\"{}\") due to {} cancellation", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    continue;
                }
            }

            if (monitoredOnly && !a.artist().monitored()) {
                LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.UNMONITORED, this, api);
                if (skipArtistSelectionEvent.cancelled()) {
                    logger.info("Unmonitored artist {} (\"{}\"), but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping artist {} (\"{}\") due to unmonitored status", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }
            if (!skipTags.isEmpty() && hasAnyTag(skipTags, a.artist().tags())) {
                LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.SKIP_TAG_FOUND, this, api);
                if (skipArtistSelectionEvent.cancelled()) {
                    logger.info("Artist {} (\"{}\") has skip-tag set, but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                } else {
                    logger.info("Skipping artist {} (\"{}\") because skip-tag is set", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }
            if (missingOnly) {
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
                if (hasFiles) {
                    LidarrSkipArtistSelectionEvent skipArtistSelectionEvent = new LidarrSkipArtistSelectionEvent(a.artist(), SelectionCancellationReason.NOT_MISSING, this, api);
                    if (skipArtistSelectionEvent.cancelled()) {
                        logger.info("Artist {} (\"{}\") not missing any track files, but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping artist {} (\"{}\") because it is not missing any track files", a.artist().id(), a.artist().artistName());
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
                    if (skipArtistSelectionEvent.cancelled()) {
                        logger.info("Artist {} (\"{}\") quality cutoff met, but {} cancelled - continuing", a.artist().id(), a.artist().artistName(), skipArtistSelectionEvent.getClass().getSimpleName());
                    } else {
                        logger.info("Skipping artist {} (\"{}\") because it meets the quality cutoff", a.artist().id(), a.artist().artistName());
                        continue;
                    }
                }
            }

            LidarrUpdateArtistEvent updateArtistEvent = new LidarrUpdateArtistEvent(a.artist(), this, api);
            if (updateArtistEvent.cancelled()) {
                logger.info("Skipping artist {} (\"{}\") due to {} cancellation", a.artist().id(), a.artist().artistName(), updateArtistEvent.getClass().getSimpleName());
                continue;
            }

            if (dryRun) {
                logger.info("Would update artist {} (\"{}\") if not in dry-run mode", a.artist().id(), a.artist().artistName());
            } else {
                logger.info("Updating artist {} (\"{}\")", a.artist().id(), a.artist().artistName());
            }
            ids.add(a.artist().id());
            arrApi.invalidate(Artist.class, a.artist().id()); // Force refresh on next
            arrApi.invalidate(Track.class, Map.of("artistId", a.artist().id())); // Force refresh on next
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
