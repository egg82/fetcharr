package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.parse.*;
import me.egg82.arr.radarr.v3.Movie;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieFileResource extends AbstractAPIObject {
    private final int id;
    private final int movieId;
    private final String relativePath;
    private final File path;
    private final long size;
    private final Instant dateAdded;
    private final String sceneName;
    private final String releaseGroup;
    private final String edition;
    private final List<@NotNull Language> languages = new ArrayList<>();
    private final QualityModel quality;
    private final List<@NotNull CustomFormatResource> customFormats = new ArrayList<>();
    private final int customFormatScore;
    private final int indexerFlags;
    private final MediaInfoResource mediaInfo;
    private final File originalFilePath;
    private final boolean qualityCutoffNotMet;

    public MovieFileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.movieId = NumberParser.getInt(-1, obj, "movieId");
        this.relativePath = StringParser.get(obj, "relativePath");
        this.path = FileParser.get(obj, "path");
        this.size = NumberParser.getLong(-1L, obj, "size");
        this.dateAdded = InstantParser.get(Instant.EPOCH, obj, "dateAdded");
        this.sceneName = StringParser.get(obj, "sceneName");
        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.edition = StringParser.get(obj, "edition");

        JSONArray languages = obj.has("languages") && obj.get("languages") != null ? obj.getJSONArray("languages") : null;
        if (languages != null) {
            for (int i = 0; i < languages.length(); i++) {
                this.languages.add(new Language(api, languages.getJSONObject(i)));
            }
        }

        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");

        JSONArray customFormats = obj.has("customFormats") && obj.get("customFormats") != null ? obj.getJSONArray("customFormats") : null;
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                this.customFormats.add(new CustomFormatResource(api, customFormats.getJSONObject(i)));
            }
        }

        this.customFormatScore = NumberParser.getInt(-1, obj, "customFormatScore");
        this.indexerFlags = NumberParser.getInt(-1, obj, "indexerFlags");
        this.mediaInfo = ObjectParser.get(MediaInfoResource.class, api, obj, "mediaInfo");
        this.originalFilePath = FileParser.get(obj, "originalFilePath");
        this.qualityCutoffNotMet = BooleanParser.get(false, obj, "qualityCutoffNotMet");
    }

    public int id() {
        return id;
    }

    public @Nullable Movie movie() {
        return api.fetch(Movie.class, movieId);
    }

    public @Nullable String relativePath() {
        return relativePath;
    }

    public @Nullable File path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @NotNull Instant dateAdded() {
        return dateAdded;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @Nullable String edition() {
        return edition;
    }

    public @NotNull List<@NotNull Language> languages() {
        return languages;
    }

    public @Nullable QualityModel quality() {
        return quality;
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

    public @Nullable File originalFilePath() {
        return originalFilePath;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieFileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MovieFileResource{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", relativePath='" + relativePath + '\'' +
                ", path=" + path +
                ", size=" + size +
                ", dateAdded=" + dateAdded +
                ", sceneName='" + sceneName + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", edition='" + edition + '\'' +
                ", languages=" + languages +
                ", quality=" + quality +
                ", customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", indexerFlags=" + indexerFlags +
                ", mediaInfo=" + mediaInfo +
                ", originalFilePath=" + originalFilePath +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
