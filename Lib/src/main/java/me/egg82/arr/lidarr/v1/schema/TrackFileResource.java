package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.lidarr.v1.Artist;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackFileResource extends AbstractAPIObject {
    private final int id;
    private final int artistId;
    private final int albumId;
    private final File path;
    private final long size;
    private final Instant dateAdded;
    private final String sceneName;
    private final String releaseGroup;
    private final QualityModel quality;
    private final int qualityWeight;
    private final List<@NotNull CustomFormatResource> customFormats = new ArrayList<>();
    private final int customFormatScore;
    private final int indexerFlags;
    private final MediaInfoResource mediaInfo;
    private final boolean qualityCutoffNotMet;
    private final ParsedTrackInfo audioTags;

    public TrackFileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.artistId = NumberParser.getInt(-1, obj, "artistId");
        this.albumId = NumberParser.getInt(-1, obj, "albumId");
        this.path = FileParser.get(obj, "path");
        this.size = NumberParser.getLong(-1L, obj, "size");
        this.dateAdded = InstantParser.get(obj, "dateAdded");
        this.sceneName = StringParser.get(obj, "sceneName");
        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");
        this.qualityWeight = NumberParser.getInt(-1, obj, "qualityWeight");

        JSONArray customFormats = obj.has("customFormats") && obj.get("customFormats") != null ? obj.getJSONArray("customFormats") : null;
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                this.customFormats.add(new CustomFormatResource(api, customFormats.getJSONObject(i)));
            }
        }

        this.customFormatScore = NumberParser.getInt(-1, obj, "customFormatScore");
        this.indexerFlags = NumberParser.getInt(-1, obj, "indexerFlags");
        this.mediaInfo = ObjectParser.get(MediaInfoResource.class, api, obj, "mediaInfo");
        this.qualityCutoffNotMet = BooleanParser.get(false, obj, "qualityCutoffNotMet");
        this.audioTags = ObjectParser.get(ParsedTrackInfo.class, api, obj, "audioTags");
    }

    public int id() {
        return id;
    }

    public @Nullable Artist artist() {
        return api.fetch(Artist.class, artistId);
    }

    public @Nullable Album album() {
        return api.fetch(Album.class, albumId);
    }

    public @Nullable File path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @Nullable Instant dateAdded() {
        return dateAdded;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @Nullable QualityModel quality() {
        return quality;
    }

    public int qualityWeight() {
        return qualityWeight;
    }

    public @NotNull List<@NotNull CustomFormatResource> customFormats() {
        return customFormats;
    }

    public int customFormatScore() {
        return customFormatScore;
    }

    public int indexerFlags() {
        return indexerFlags;
    }

    public @Nullable MediaInfoResource mediaInfo() {
        return mediaInfo;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    public @Nullable ParsedTrackInfo audioTags() {
        return audioTags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TrackFileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrackFileResource{" +
                "id=" + id +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                ", path=" + path +
                ", size=" + size +
                ", dateAdded=" + dateAdded +
                ", sceneName='" + sceneName + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", quality=" + quality +
                ", qualityWeight=" + qualityWeight +
                ", customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", indexerFlags=" + indexerFlags +
                ", mediaInfo=" + mediaInfo +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", audioTags=" + audioTags +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
