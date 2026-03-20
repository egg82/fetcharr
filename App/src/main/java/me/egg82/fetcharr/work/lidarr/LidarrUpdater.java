package me.egg82.fetcharr.work.lidarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.config.Tristate;
import me.egg82.arr.lidarr.LidarrV1API;
import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.lidarr.v1.Artist;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.lidarr.v1.Track;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.lidarr.v1.schema.TagResource;
import me.egg82.arr.lidarr.v1.schema.TrackFileResource;
import me.egg82.arr.lidarr.v1.schema.TrackResource;
import me.egg82.arr.unit.TimeValue;
import me.egg82.fetcharr.env.CommonConfigVars;
import me.egg82.fetcharr.env.LidarrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LidarrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedArtist> random = new WeightedRandom<>();

    public LidarrUpdater(@NotNull LidarrV1API api) {
        super(api);

        if (!Instant.EPOCH.equals(this.metaFile.lastUpdate())) {
            logger.debug("Resuming LIDARR_{} from last update at {}", api.id(), this.metaFile.lastUpdate());
        }
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
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for LIDARR_{}: {}", searchAmount, api.id(), api.baseUrl());
            return;
        }

        logger.info("Updating up to {} items for for LIDARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        Artist allArtists = api.fetch(Artist.class);
        if (allArtists == null) {
            logger.error("LIDARR_{} returned bad result for {}", api.id(), Artist.UNKNOWN.apiPath());
            return;
        }

        List<WeightedArtist> wrapped = new ArrayList<>();
        for (ArtistResource s : allArtists.resources()) {
            Album allAlbums = api.fetch(Album.class, Map.of("artistId", s.id()));
            if (allAlbums == null) {
                logger.warn("LIDARR_{} returned bad result for {}", api.id(), Album.UNKNOWN.apiPath());
                continue;
            }

            wrapped.add(new WeightedArtist(s, allAlbums.resources()));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = LidarrConfigVars.getBool(LidarrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = LidarrConfigVars.getBool(LidarrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = LidarrConfigVars.getBool(LidarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = LidarrConfigVars.getArr(LidarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = CommonConfigVars.getBool(CommonConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            WeightedArtist a = random.selectOne();
            if (a == null) {
                continue;
            }
            if (monitoredOnly && !a.artist().monitored()) {
                logger.info("Skipping artist {} (\"{}\") due to unmonitored status", a.artist().id(), a.artist().artistName());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, a.artist().tags())) {
                logger.info("Skipping artist {} (\"{}\") because skip-tag is set", a.artist().id(), a.artist().artistName());
                continue;
            }
            if (missingOnly) {
                Track allTracks = api.fetch(Track.class, Map.of("artistId", a.artist().id()));
                if (allTracks == null) {
                    logger.warn("LIDARR_{} returned bad result for {}", api.id(), Track.UNKNOWN.apiPath());
                    continue;
                }

                boolean hasFiles = true;
                for (TrackResource t : allTracks.resources()) {
                    if (!t.hasFile()) {
                        hasFiles = false;
                        break;
                    }
                }
                if (hasFiles) {
                    logger.info("Skipping artist {} (\"{}\") because it is not missing any music files", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }
            if (useCutoff) {
                Track allTracks = api.fetch(Track.class, Map.of("artistId", a.artist().id()));
                if (allTracks == null) {
                    logger.warn("LIDARR_{} returned bad result for {}", api.id(), Track.UNKNOWN.apiPath());
                    continue;
                }

                boolean cutoffMet = true;
                for (TrackResource t : allTracks.resources()) {
                    TrackFileResource trackFile = t.trackFile();
                    if (trackFile != null && !trackFile.qualityCutoffNotMet()) {
                        cutoffMet = false;
                        break;
                    }
                }
                if (cutoffMet) {
                    logger.info("Skipping artist {} (\"{}\") because it meets the quality cutoff", a.artist().id(), a.artist().artistName());
                    continue;
                }
            }

            if (dryRun) {
                logger.info("Would update artist {} (\"{}\") if not in dry-run mode", a.artist().id(), a.artist().artistName());
            } else {
                logger.info("Updating artist {} (\"{}\")", a.artist().id(), a.artist().artistName());
            }
            ids.add(a.artist().id());
            api.invalidate(Artist.class, a.artist().id()); // Force refresh on next
            api.invalidate(Track.class, Map.of("artistId", a.artist().id())); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        random.clear();

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
