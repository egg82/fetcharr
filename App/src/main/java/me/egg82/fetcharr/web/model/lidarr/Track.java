package me.egg82.fetcharr.web.model.lidarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.BooleanParser;
import me.egg82.fetcharr.parse.DurationParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Track extends AbstractAPIObject<Track> {
    public static final Track UNKNOWN = new Track(ArrAPI.UNKNOWN, -1);

    private final int id;

    private Artist artist;
    private String foreignTrackId;
    private String foreignRecordingId;
    private TrackFile trackFile;
    private Album album;
    private boolean explicit;
    private int absoluteTrackNumber;
    private String trackNumber;
    private String title;
    private Duration duration;
    private int mediumNumber;
    private boolean hasFile;
    private Ratings ratings;

    public Track(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/track/" + id);
        this.id = id;
    }

    @Override
    public Track fetch(@NotNull String apiKey) {
        if (this.id < 0 || !this.fetching.compareAndSet(false, true)) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile(id));
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile(id);
            try {
                parse(data.read());
                if (this.title != null && !this.title.isBlank()) {
                    this.fetched = meta.fetched();
                    this.fetching.set(false);
                    return this;
                }
            } catch (Exception ex) {
                logger.warn("Could not read data from {}", data.path(), ex);
            }
        }

        JsonNode node = get(apiKey);
        if (node == null) {
            logger.debug("Could not read data from {}", url());
            // Not setting fetched = invalid
            this.fetching.set(false);
            return this;
        }

        try {
            parse(node);
        } catch (Exception ex) {
            logger.warn("Could not read data from {}", url(), ex);
            this.fetching.set(false);
            return this;
        }

        this.fetched = Instant.now();
        try {
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
        this.fetching.set(false);
        return this;
    }

    @Override
    public boolean valid() {
        if (this.fetched == null) {
            return false;
        }

        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        if (!useCache) {
            return false;
        }

        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);
        return this.fetched.plus(cacheTime.duration()).isAfter(Instant.now());
    }

    @Override
    public boolean unknown() {
        return this.id < 0;
    }

    @Override
    public void invalidate() {
        try {
            cacheFile(id).delete();
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}-{}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), id, ex);
        }
        this.fetched = null;
    }

    @Override
    protected void parse(@NotNull JsonNode data) {
        JSONObject obj = data.getObject();

        if (obj == null || obj.isEmpty()) {
            return;
        }

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "artistId"));
        this.artist = id >= 0 ? api.fetch(Artist.class, id, true) : null;

        this.foreignTrackId = StringParser.parse(obj, "foreignTrackId");
        this.foreignRecordingId = StringParser.parse(obj, "foreignRecordingId");

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "trackFileId"));
        this.trackFile = id >= 0 ? api.fetch(TrackFile.class, id, true) : null;

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "albumId"));
        this.album = id >= 0 ? api.fetch(Album.class, id, true) : null;

        this.explicit = BooleanParser.parse(false, StringParser.parse(obj, "explicit"));
        this.absoluteTrackNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "absoluteTrackNumber"));
        this.trackNumber = StringParser.parse(obj, "trackNumber");
        this.title = StringParser.parse(obj, "title");
        this.duration = DurationParser.parse(StringParser.parse(obj, "duration"));
        this.mediumNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "mediumNumber"));
        this.hasFile = BooleanParser.parse(false, StringParser.parse(obj, "hasFile"));
        this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public @Nullable Artist artist() {
        return artist;
    }

    public @Nullable String foreignTrackId() {
        return foreignTrackId;
    }

    public @Nullable String foreignRecordingId() {
        return foreignRecordingId;
    }

    public @NotNull TrackFile trackFile() {
        return trackFile;
    }

    public @Nullable Album album() {
        return album;
    }

    public boolean explicit() {
        return explicit;
    }

    public int absoluteTrackNumber() {
        return absoluteTrackNumber;
    }

    public @Nullable String trackNumber() {
        return trackNumber;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public int mediumNumber() {
        return mediumNumber;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track track)) return false;
        return id == track.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", artist=" + artist +
                ", foreignTrackId='" + foreignTrackId + '\'' +
                ", foreignRecordingId='" + foreignRecordingId + '\'' +
                ", trackFile=" + trackFile +
                ", album=" + album +
                ", explicit=" + explicit +
                ", absoluteTrackNumber=" + absoluteTrackNumber +
                ", trackNumber=" + trackNumber +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", mediumNumber=" + mediumNumber +
                ", hasFile=" + hasFile +
                ", ratings=" + ratings +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }
}
