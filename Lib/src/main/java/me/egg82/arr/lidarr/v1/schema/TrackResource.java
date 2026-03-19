package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

public class TrackResource extends AbstractAPIObject {
    private final int id;
    private final String foreignTrackId;
    private final String foreignRecordingId;
    private final int albumId;
    private final boolean explicit;
    private final int absoluteTrackNumber;
    private final String trackNumber;
    private final String title;
    private final Duration duration;
    private final TrackFileResource trackFile;
    private final int mediumNumber;
    private final boolean hasFile;
    private final ArtistResource artist;
    private final Ratings ratings;

    public TrackResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.foreignTrackId = StringParser.get(obj, "foreignTrackId");
        this.foreignRecordingId = StringParser.get(obj, "foreignRecordingId");
        this.albumId = NumberParser.getInt(-1, obj, "albumId");
        this.explicit = BooleanParser.get(false, obj, "explicit");
        this.absoluteTrackNumber = NumberParser.getInt(-1, obj, "absoluteTrackNumber");
        this.trackNumber = StringParser.get(obj, "trackNumber");
        this.title = StringParser.get(obj, "title");
        this.duration = DurationParser.get(obj, "duration");
        this.trackFile = ObjectParser.get(TrackFileResource.class, api, obj, "trackFile");
        this.mediumNumber = NumberParser.getInt(-1, obj, "mediumNumber");
        this.hasFile = BooleanParser.get(false, obj, "hasFile");
        this.artist = ObjectParser.get(ArtistResource.class, api, obj, "artist");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
    }

    public int id() {
        return id;
    }

    public @Nullable String foreignTrackId() {
        return foreignTrackId;
    }

    public @Nullable String foreignRecordingId() {
        return foreignRecordingId;
    }

    public @Nullable Album album() {
        return api.fetch(Album.class, albumId);
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

    public @Nullable TrackFileResource trackFile() {
        return trackFile;
    }

    public int mediumNumber() {
        return mediumNumber;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public @Nullable ArtistResource artist() {
        return artist;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TrackResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrackResource{" +
                "id=" + id +
                ", foreignTrackId='" + foreignTrackId + '\'' +
                ", foreignRecordingId='" + foreignRecordingId + '\'' +
                ", albumId=" + albumId +
                ", explicit=" + explicit +
                ", absoluteTrackNumber=" + absoluteTrackNumber +
                ", trackNumber='" + trackNumber + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", trackFile=" + trackFile +
                ", mediumNumber=" + mediumNumber +
                ", hasFile=" + hasFile +
                ", artist=" + artist +
                ", ratings=" + ratings +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
