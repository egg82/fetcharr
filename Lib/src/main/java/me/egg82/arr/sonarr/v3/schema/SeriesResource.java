package me.egg82.arr.sonarr.v3.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.sonarr.v3.QualityProfile;
import me.egg82.arr.sonarr.v3.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SeriesResource extends AbstractAPIObject {
    private final Instant added;
    private final AddSeriesOptions addOptions;
    private final Duration airTime;
    private final PVector<@NotNull AlternateTitleResource> alternateTitles;
    private final String certification;
    private final String cleanTitle;
    private final boolean ended;
    private final boolean episodesChanged;
    private final Instant firstAired;
    private final File folder;
    private final Set<@NotNull String> genres = new HashSet<>();
    private final int id;
    private final PVector<@NotNull MediaCover> images;
    private final String imdbId;
    private final Instant lastAired;
    private final boolean monitored;
    private final NewItemMonitorTypes monitorNewItems;
    private final String network;
    private final Instant nextAiring;
    private final Language originalLanguage;
    private final String overview;
    private final File path;
    private final Instant previousAiring;
    private final String profileName;
    private final int qualityProfileId;
    private final Ratings ratings;
    private final String remotePoster;
    private final File rootFolderPath;
    private final Duration runtime;
    private final boolean seasonFolder;
    private final PVector<@NotNull SeasonResource> seasons;
    private final SeriesType seriesType;
    private final String sortTitle;
    private final SeriesStatisticsResource statistics;
    private final SeriesStatusType status;
    private final IntSet tags = new IntArraySet();
    private final String title;
    private final String titleSlug;
    private final int tmdbId;
    private final int tvdbId;
    private final int tvMazeId;
    private final int tvRageId;
    private final boolean useSceneNumbering;
    private final int year;

    public SeriesResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.added = InstantParser.get(obj, "added");
        this.addOptions = ObjectParser.get(AddSeriesOptions.class, api, obj, "addOptions");
        this.airTime = DurationParser.get(obj, "airTime");

        JSONArray alternateTitles = obj.has("alternateTitles") && obj.get("alternateTitles") != null ? obj.getJSONArray("alternateTitles") : null;
        List<@NotNull AlternateTitleResource> alternateTitlesL = new ArrayList<>();
        if (alternateTitles != null) {
            for (int i = 0; i < alternateTitles.length(); i++) {
                alternateTitlesL.add(new AlternateTitleResource(api, alternateTitles.getJSONObject(i)));
            }
        }
        this.alternateTitles = TreePVector.from(alternateTitlesL);

        this.certification = StringParser.get(obj, "certification");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");
        this.ended = BooleanParser.get(false, obj, "ended");
        this.episodesChanged = BooleanParser.get(false, obj, "episodesChanged");
        this.firstAired = InstantParser.get(obj, "firstAired");
        this.folder = FileParser.get(obj, "folder");

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        this.id = NumberParser.getInt(-1, obj, "id");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        this.imdbId = StringParser.get(obj, "imdbId");
        this.lastAired = InstantParser.get(obj, "lastAired");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.monitorNewItems = NewItemMonitorTypes.get(NewItemMonitorTypes.NONE, obj, "monitorNewItems");
        this.network = StringParser.get(obj, "network");
        this.nextAiring = InstantParser.get(obj, "nextAiring");
        this.originalLanguage = ObjectParser.get(Language.class, api, obj, "originalLanguage");
        this.overview = StringParser.get(obj, "overview");
        this.path = FileParser.get(obj, "path");
        this.previousAiring = InstantParser.get(obj, "previousAiring");
        this.profileName = StringParser.get(obj, "profileName");
        this.qualityProfileId = NumberParser.getInt(-1, obj, "qualityProfileId");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.remotePoster = StringParser.get(obj, "remotePoster");
        this.rootFolderPath = FileParser.get(obj, "rootFolderPath");
        this.runtime = DurationParser.get(obj, "runtime");
        this.seasonFolder = BooleanParser.get(false, obj, "seasonFolder");

        JSONArray seasons = obj.has("seasons") && obj.get("seasons") != null ? obj.getJSONArray("seasons") : null;
        List<@NotNull SeasonResource> seasonsL = new ArrayList<>();
        if (seasons != null) {
            for (int i = 0; i < seasons.length(); i++) {
                seasonsL.add(new SeasonResource(api, seasons.getJSONObject(i)));
            }
        }
        this.seasons = TreePVector.from(seasonsL);

        this.seriesType = SeriesType.get(SeriesType.STANDARD, obj, "seriesType");
        this.sortTitle = StringParser.get(obj, "sortTitle");
        this.statistics = ObjectParser.get(SeriesStatisticsResource.class, api, obj, "statistics");
        this.status = SeriesStatusType.get(SeriesStatusType.DELETED, obj, "status");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        this.title = StringParser.get(obj, "title");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.tmdbId = NumberParser.getInt(-1, obj, "tmdbId");
        this.tvdbId = NumberParser.getInt(-1, obj, "tvdbId");
        this.tvMazeId = NumberParser.getInt(-1, obj, "tvMazeId");
        this.tvRageId = NumberParser.getInt(-1, obj, "tvRageId");
        this.useSceneNumbering = BooleanParser.get(false, obj, "useSceneNumbering");
        this.year = NumberParser.getInt(-1, obj, "year");
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddSeriesOptions addOptions() {
        return addOptions;
    }

    public @Nullable Duration airTime() {
        return airTime;
    }

    public @NotNull PVector<@NotNull AlternateTitleResource> alternateTitles() {
        return alternateTitles;
    }

    public @Nullable String certification() {
        return certification;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public boolean ended() {
        return ended;
    }

    public boolean episodesChanged() {
        return episodesChanged;
    }

    public @Nullable Instant firstAired() {
        return firstAired;
    }

    public @Nullable File folder() {
        return folder;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public int id() {
        return id;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable String imdbId() {
        return imdbId;
    }

    public @Nullable Instant lastAired() {
        return lastAired;
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull NewItemMonitorTypes monitorNewItems() {
        return monitorNewItems;
    }

    public @Nullable String network() {
        return network;
    }

    public @Nullable Instant nextAiring() {
        return nextAiring;
    }

    public @Nullable Language originalLanguage() {
        return originalLanguage;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable Instant previousAiring() {
        return previousAiring;
    }

    public @Nullable String profileName() {
        return profileName;
    }

    public @Nullable QualityProfile qualityProfile() {
        return api.fetch(QualityProfile.class, qualityProfileId);
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable Duration runtime() {
        return runtime;
    }

    public boolean seasonFolder() {
        return seasonFolder;
    }

    public @NotNull PVector<@NotNull SeasonResource> seasons() {
        return seasons;
    }

    public @NotNull SeriesType seriesType() {
        return seriesType;
    }

    public @Nullable String sortTitle() {
        return sortTitle;
    }

    public @Nullable SeriesStatisticsResource statistics() {
        return statistics;
    }

    public @NotNull SeriesStatusType status() {
        return status;
    }

    public @NotNull PVector<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return TreePVector.from(r);
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public int tmdbId() {
        return tmdbId;
    }

    public int tvdbId() {
        return tvdbId;
    }

    public int tvMazeId() {
        return tvMazeId;
    }

    public int tvRageId() {
        return tvRageId;
    }

    public boolean useSceneNumbering() {
        return useSceneNumbering;
    }

    public int year() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeriesResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SeriesResource{" +
                "added=" + added +
                ", addOptions=" + addOptions +
                ", airTime=" + airTime +
                ", alternateTitles=" + alternateTitles +
                ", certification='" + certification + '\'' +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", ended=" + ended +
                ", episodesChanged=" + episodesChanged +
                ", firstAired=" + firstAired +
                ", folder=" + folder +
                ", genres=" + genres +
                ", id=" + id +
                ", images=" + images +
                ", imdbId='" + imdbId + '\'' +
                ", lastAired=" + lastAired +
                ", monitored=" + monitored +
                ", monitorNewItems=" + monitorNewItems +
                ", network='" + network + '\'' +
                ", nextAiring=" + nextAiring +
                ", originalLanguage=" + originalLanguage +
                ", overview='" + overview + '\'' +
                ", path=" + path +
                ", previousAiring=" + previousAiring +
                ", profileName='" + profileName + '\'' +
                ", qualityProfileId=" + qualityProfileId +
                ", ratings=" + ratings +
                ", remotePoster='" + remotePoster + '\'' +
                ", rootFolderPath=" + rootFolderPath +
                ", runtime=" + runtime +
                ", seasonFolder=" + seasonFolder +
                ", seasons=" + seasons +
                ", seriesType=" + seriesType +
                ", sortTitle='" + sortTitle + '\'' +
                ", statistics=" + statistics +
                ", status=" + status +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", tmdbId=" + tmdbId +
                ", tvdbId=" + tvdbId +
                ", tvMazeId=" + tvMazeId +
                ", tvRageId=" + tvRageId +
                ", useSceneNumbering=" + useSceneNumbering +
                ", year=" + year +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
