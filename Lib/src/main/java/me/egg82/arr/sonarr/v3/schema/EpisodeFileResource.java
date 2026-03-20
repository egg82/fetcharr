package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.sonarr.v3.Series;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpisodeFileResource extends AbstractAPIObject {
    private final PVector<@NotNull CustomFormatResource> customFormats;
    private final int customFormatScore;
    private final Instant dateAdded;
    private final int id;
    private final int indexerFlags;
    private final PVector<@NotNull Language> languages;
    private final MediaInfoResource mediaInfo;
    private final File path;
    private final QualityModel quality;
    private final boolean qualityCutoffNotMet;
    private final String relativePath;
    private final String releaseGroup;
    private final ReleaseType releaseType;
    private final String sceneName;
    private final int seasonNumber;
    private final int seriesId;
    private final long size;

    public EpisodeFileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        JSONArray customFormats = obj.has("customFormats") && obj.get("customFormats") != null ? obj.getJSONArray("customFormats") : null;
        List<@NotNull CustomFormatResource> customFormatsL = new ArrayList<>();
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                customFormatsL.add(new CustomFormatResource(api, customFormats.getJSONObject(i)));
            }
        }
        this.customFormats = TreePVector.from(customFormatsL);

        this.customFormatScore = NumberParser.getInt(-1, obj, "customFormatScore");
        this.dateAdded = InstantParser.get(Instant.EPOCH, obj, "dateAdded");
        this.id = NumberParser.getInt(-1, obj, "id");
        this.indexerFlags = NumberParser.getInt(-1, obj, "indexerFlags");

        JSONArray languages = obj.has("languages") && obj.get("languages") != null ? obj.getJSONArray("languages") : null;
        List<@NotNull Language> languagesL = new ArrayList<>();
        if (languages != null) {
            for (int i = 0; i < languages.length(); i++) {
                languagesL.add(new Language(api, languages.getJSONObject(i)));
            }
        }
        this.languages = TreePVector.from(languagesL);

        this.mediaInfo = ObjectParser.get(MediaInfoResource.class, api, obj, "mediaInfo");
        this.path = FileParser.get(obj, "path");
        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");
        this.qualityCutoffNotMet = BooleanParser.get(false, obj, "qualityCutoffNotMet");
        this.relativePath = StringParser.get(obj, "relativePath");
        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.releaseType = ReleaseType.get(ReleaseType.UNKNOWN, obj, "releaseType");
        this.sceneName = StringParser.get(obj, "sceneName");
        this.seasonNumber = NumberParser.getInt(-1, obj, "seasonNumber");
        this.seriesId = NumberParser.getInt(-1, obj, "seriesId");
        this.size = NumberParser.getLong(-1L, obj, "size");
    }

    public @NotNull PVector<@NotNull CustomFormatResource> customFormats() {
        return customFormats;
    }

    public int customFormatScore() {
        return customFormatScore;
    }

    public @Nullable Instant dateAdded() {
        return dateAdded;
    }

    public int id() {
        return id;
    }

    public int indexerFlags() {
        return indexerFlags;
    }

    public @NotNull PVector<@NotNull Language> languages() {
        return languages;
    }

    public @Nullable MediaInfoResource mediaInfo() {
        return mediaInfo;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityModel quality() {
        return quality;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    public @Nullable String relativePath() {
        return relativePath;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @NotNull ReleaseType releaseType() {
        return releaseType;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public int seasonNumber() {
        return seasonNumber;
    }

    public @Nullable Series series() {
        return api.fetch(Series.class, seriesId);
    }

    public long size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EpisodeFileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EpisodeFileResource{" +
                "customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", dateAdded=" + dateAdded +
                ", id=" + id +
                ", indexerFlags=" + indexerFlags +
                ", languages=" + languages +
                ", mediaInfo=" + mediaInfo +
                ", path=" + path +
                ", quality=" + quality +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", relativePath='" + relativePath + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", releaseType=" + releaseType +
                ", sceneName='" + sceneName + '\'' +
                ", seasonNumber=" + seasonNumber +
                ", seriesId=" + seriesId +
                ", size=" + size +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
