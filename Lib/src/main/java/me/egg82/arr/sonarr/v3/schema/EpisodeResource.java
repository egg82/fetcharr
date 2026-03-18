package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpisodeResource extends AbstractAPIObject {
    private final int absoluteEpisodeNumber;
    private final Instant airDate;
    private final Instant airDateUtc;
    private final Instant endTime;
    private final EpisodeFileResource episodeFile;
    private final int episodeNumber;
    private final String finaleType;
    private final Instant grabDate;
    private final boolean hasFile;
    private final int id;
    private final List<@NotNull MediaCover> images = new ArrayList<>();
    private final Instant lastSearchTime;
    private final boolean monitored;
    private final String overview;
    private final Duration runtime;
    private final int sceneAbsoluteEpisodeNumber;
    private final int sceneEpisodeNumber;
    private final int sceneSeasonNumber;
    private final int seasonNumber;
    private final SeriesResource series;
    private final String title;
    private final int tvdbId;
    private final boolean unverifiedSceneNumbering;

    public EpisodeResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.absoluteEpisodeNumber = NumberParser.getInt(-1, obj, "absoluteEpisodeNumber");
        this.airDate = InstantParser.get(obj, "airDate");
        this.airDateUtc = InstantParser.get(obj, "airDateUtc");
        this.endTime = InstantParser.get(obj, "endTime");
        this.episodeFile = ObjectParser.get(EpisodeFileResource.class, api, obj, "episodeFile");
        this.episodeNumber = NumberParser.getInt(-1, obj, "episodeNumber");
        this.finaleType = StringParser.get(obj, "finaleType");
        this.grabDate = InstantParser.get(obj, "grabDate");
        this.hasFile = BooleanParser.get(false, obj, "hasFile");
        this.id = NumberParser.getInt(-1, obj, "id");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }

        this.lastSearchTime = InstantParser.get(Instant.EPOCH, obj, "lastSearchTime");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.overview = StringParser.get(obj, "overview");
        this.runtime = DurationParser.get(obj, "runtime");
        this.sceneAbsoluteEpisodeNumber = NumberParser.getInt(-1, obj, "sceneAbsoluteEpisodeNumber");
        this.sceneEpisodeNumber = NumberParser.getInt(-1, obj, "sceneEpisodeNumber");
        this.sceneSeasonNumber = NumberParser.getInt(-1, obj, "sceneSeasonNumber");
        this.seasonNumber = NumberParser.getInt(-1, obj, "seasonNumber");
        this.series = ObjectParser.get(SeriesResource.class, api, obj, "series");
        this.title = StringParser.get(obj, "title");
        this.tvdbId = NumberParser.getInt(-1, obj, "tvdbId");
        this.unverifiedSceneNumbering = BooleanParser.get(false, obj, "unverifiedSceneNumbering");
    }

    public int absoluteEpisodeNumber() {
        return absoluteEpisodeNumber;
    }

    public @Nullable Instant airDate() {
        return airDate;
    }

    public @Nullable Instant airDateUtc() {
        return airDateUtc;
    }

    public @Nullable Instant endTime() {
        return endTime;
    }

    public @Nullable EpisodeFileResource episodeFile() {
        return episodeFile;
    }

    public int episodeNumber() {
        return episodeNumber;
    }

    public @Nullable String finaleType() {
        return finaleType;
    }

    public @Nullable Instant grabDate() {
        return grabDate;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public int id() {
        return id;
    }

    public @NotNull List<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable Instant lastSearchTime() {
        return lastSearchTime;
    }

    public boolean monitored() {
        return monitored;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable Duration runtime() {
        return runtime;
    }

    public int sceneAbsoluteEpisodeNumber() {
        return sceneAbsoluteEpisodeNumber;
    }

    public int sceneEpisodeNumber() {
        return sceneEpisodeNumber;
    }

    public int sceneSeasonNumber() {
        return sceneSeasonNumber;
    }

    public int seasonNumber() {
        return seasonNumber;
    }

    public @Nullable SeriesResource series() {
        return series;
    }

    public @Nullable String title() {
        return title;
    }

    public int tvdbId() {
        return tvdbId;
    }

    public boolean unverifiedSceneNumbering() {
        return unverifiedSceneNumbering;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EpisodeResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EpisodeResource{" +
                "absoluteEpisodeNumber=" + absoluteEpisodeNumber +
                ", airDate=" + airDate +
                ", airDateUtc=" + airDateUtc +
                ", endTime=" + endTime +
                ", episodeFile=" + episodeFile +
                ", episodeNumber=" + episodeNumber +
                ", finaleType='" + finaleType + '\'' +
                ", grabDate=" + grabDate +
                ", hasFile=" + hasFile +
                ", id=" + id +
                ", images=" + images +
                ", lastSearchTime=" + lastSearchTime +
                ", monitored=" + monitored +
                ", overview='" + overview + '\'' +
                ", runtime=" + runtime +
                ", sceneAbsoluteEpisodeNumber=" + sceneAbsoluteEpisodeNumber +
                ", sceneEpisodeNumber=" + sceneEpisodeNumber +
                ", sceneSeasonNumber=" + sceneSeasonNumber +
                ", seasonNumber=" + seasonNumber +
                ", series=" + series +
                ", title='" + title + '\'' +
                ", tvdbId=" + tvdbId +
                ", unverifiedSceneNumbering=" + unverifiedSceneNumbering +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
