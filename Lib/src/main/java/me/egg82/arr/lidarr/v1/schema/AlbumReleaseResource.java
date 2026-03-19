package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.DurationParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class AlbumReleaseResource extends AbstractAPIObject {
    private final int id;
    private final int albumId;
    private final String foreignReleaseId;
    private final String title;
    private final String status;
    private final Duration duration;
    private final int trackCount;
    private final List<@NotNull MediumResource> media = new ArrayList<>();
    private final int mediumCount;
    private final String disambiguation;
    private final Set<@NotNull String> country = new HashSet<>();
    private final Set<@NotNull String> label = new HashSet<>();
    private final String format;
    private final boolean monitored;

    public AlbumReleaseResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.albumId = NumberParser.getInt(-1, obj, "albumId");
        this.foreignReleaseId = StringParser.get(obj, "foreignReleaseId");
        this.title = StringParser.get(obj, "title");
        this.status = StringParser.get(obj, "status");
        this.duration = DurationParser.get(obj, "duration");
        this.trackCount = NumberParser.getInt(-1, obj, "trackCount");

        JSONArray media = obj.has("media") && obj.get("media") != null ? obj.getJSONArray("media") : null;
        if (media != null) {
            for (int i = 0; i < media.length(); i++) {
                this.media.add(new MediumResource(api, media.getJSONObject(i)));
            }
        }

        this.mediumCount = NumberParser.getInt(-1, obj, "mediumCount");
        this.disambiguation = StringParser.get(obj, "disambiguation");

        JSONArray country = obj.has("country") && obj.get("country") != null ? obj.getJSONArray("country") : null;
        if (country != null) {
            for (int i = 0; i < country.length(); i++) {
                this.country.add(country.getString(i));
            }
        }

        JSONArray label = obj.has("label") && obj.get("label") != null ? obj.getJSONArray("label") : null;
        if (label != null) {
            for (int i = 0; i < label.length(); i++) {
                this.label.add(label.getString(i));
            }
        }

        this.format = StringParser.get(obj, "format");
        this.monitored = BooleanParser.get(false, obj, "monitored");
    }

    public int id() {
        return id;
    }

    public @Nullable Album album() {
        return api.fetch(Album.class, albumId);
    }

    public @Nullable String foreignReleaseId() {
        return foreignReleaseId;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String status() {
        return status;
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public int trackCount() {
        return trackCount;
    }

    public @NotNull List<@NotNull MediumResource> media() {
        return media;
    }

    public int mediumCount() {
        return mediumCount;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @NotNull Set<@NotNull String> country() {
        return country;
    }

    public @NotNull Set<@NotNull String> label() {
        return label;
    }

    public @Nullable String format() {
        return format;
    }

    public boolean monitored() {
        return monitored;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlbumReleaseResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlbumReleaseResource{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", foreignReleaseId='" + foreignReleaseId + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", duration=" + duration +
                ", trackCount=" + trackCount +
                ", media=" + media +
                ", mediumCount=" + mediumCount +
                ", disambiguation='" + disambiguation + '\'' +
                ", country=" + country +
                ", label=" + label +
                ", format='" + format + '\'' +
                ", monitored=" + monitored +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
