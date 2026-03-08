package me.egg82.fetcharr.web.model.sonarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.util.Weighted;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Series extends AbstractAPIObject<Series> implements Weighted {
    public static Series UNKNOWN = new Series(ArrAPI.UNKNOWN, -1);

    private final int id;

    private Instant added;
    private AddSeriesOptions addOptions;
    private Duration airTime;
    private final Set<@NotNull AlternateTitle> alternateTitles = new HashSet<>();
    private String certification;
    private String cleanTitle;
    private boolean ended;
    private boolean episodesChanged;
    private Instant firstAired;
    private File folder;
    private final Set<@NotNull String> genres = new HashSet<>();
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private String imdbId;
    private Instant lastAired;
    private boolean monitored;
    private SonarrMonitorType monitorNewItems;
    private String network;
    private Instant nextAiring;
    private Language originalLanguage;
    private String overview;
    private File path;
    private Instant previousAiring;
    private String profileName;
    private QualityProfile qualityProfile;
    private Ratings ratings;
    private String remotePoster;
    private File rootFolderPath;
    private Duration runtime;
    private boolean seasonFolder;
    private Set<@NotNull Season> seasons = new HashSet<>();
    private SonarrSeriesType seriesType;
    private String sortTitle;
    private Statistics statistics;
    private SonarrSeriesStatus status;
    private final Set<@NotNull Tag> tags = new HashSet<>();
    private String title;
    private String titleSlug;
    private int tmdbId;
    private int tvdbId;
    private int tvMazeId;
    private int tvRageId;
    private boolean useSceneNumbering;
    private int year;

    private Instant lastSelected = Instant.EPOCH;

    public Series(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/series/" + id);
        this.id = id;
    }

    @Override
    public Series fetch(@NotNull String apiKey) {
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
                    this.lastSelected = meta.selected();
                    this.fetching.set(false);
                    return this;
                }
            } catch (Exception ex) {
                logger.warn("Could not read data from {}", data.path(), ex);
            }
        }

        JsonNode node = get(apiKey);
        if (node == null) {
            logger.warn("Could not read data from {}", url());
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
            metaFile(id).delete();
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}-{}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), id, ex);
        }
    }

    @Override
    protected void parse(@NotNull JsonNode data) {
        JSONObject obj = data.getObject();

        if (obj == null || obj.isEmpty()) {
            return;
        }

        this.added = InstantParser.parse(Instant.now(), StringParser.parse(obj, "added"));
        this.addOptions = obj.has("addOptions") ? new AddSeriesOptions(obj.getJSONObject("addOptions")) : null;
        this.airTime = DurationParser.parse(StringParser.parse(obj, "airTime"));

        this.alternateTitles.clear();
        JSONArray alternateTitles = obj.has("alternateTitles") ? obj.getJSONArray("alternateTitles") : null;
        if (alternateTitles != null) {
            for (int i = 0; i < alternateTitles.length(); i++) {
                this.alternateTitles.add(new AlternateTitle(alternateTitles.getJSONObject(i)));
            }
        }

        this.certification = StringParser.parse(obj, "certification");
        this.cleanTitle = StringParser.parse(obj, "cleanTitle");
        this.ended = BooleanParser.parse(false, StringParser.parse(obj, "ended"));
        this.episodesChanged = BooleanParser.parse(false, StringParser.parse(obj, "episodesChanged"));
        this.firstAired = InstantParser.parse(StringParser.parse(obj, "firstAired"));
        this.folder = FileParser.parse(StringParser.parse(obj, "folder"));

        this.genres.clear();
        JSONArray genres = obj.has("genres") ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.imdbId = StringParser.parse(obj, "imdbId");
        this.lastAired = InstantParser.parse(StringParser.parse(obj, "lastAired"));
        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.monitorNewItems = SonarrMonitorType.parse(SonarrMonitorType.NONE, StringParser.parse(obj, "monitorNewItems"));
        this.network = StringParser.parse(obj, "network");
        this.nextAiring = InstantParser.parse(StringParser.parse(obj, "nextAiring"));

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "originalLanguage"));
        this.originalLanguage = id >= 0 ? api.fetch(Language.class, id) : Language.UNKNOWN;

        this.overview = StringParser.parse(obj, "overview");
        this.path = FileParser.parse(StringParser.parse(obj, "path"));
        this.previousAiring = InstantParser.parse(StringParser.parse(obj, "previousAiring"));
        this.profileName = StringParser.parse(obj, "profileName");

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "qualityProfile"));
        this.qualityProfile = id >= 0 ? api.fetch(QualityProfile.class, id) : QualityProfile.UNKNOWN;

        this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;
        this.remotePoster = StringParser.parse(obj, "remotePoster");
        this.rootFolderPath = FileParser.parse(StringParser.parse(obj, "rootFolderPath"));
        this.runtime = Duration.ofSeconds(NumberParser.parseInt(-1, StringParser.parse(obj, "runtime")));
        this.seasonFolder = BooleanParser.parse(false, StringParser.parse(obj, "seasonFolder"));

        this.seasons.clear();
        JSONArray seasons = obj.has("seasons") ? obj.getJSONArray("seasons") : null;
        if (seasons != null) {
            for (int i = 0; i < seasons.length(); i++) {
                this.seasons.add(new Season(seasons.getJSONObject(i)));
            }
        }

        this.seriesType = SonarrSeriesType.parse(SonarrSeriesType.DAILY, StringParser.parse(obj, "seriesType"));
        this.sortTitle = StringParser.parse(obj, "sortTitle");
        this.statistics = obj.has("statistics") ? new Statistics(obj.getJSONObject("statistics")) : null;
        this.status = SonarrSeriesStatus.parse(SonarrSeriesStatus.DELETED, StringParser.parse(obj, "status"));

        this.tags.clear();
        JSONArray tags = obj.has("tags") ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                id = NumberParser.parseInt(-1, tags.getString(i));
                if (id >= 0) {
                    this.tags.add(api.fetch(Tag.class, id));
                }
            }
        }

        this.titleSlug = StringParser.parse(obj, "titleSlug");
        this.tmdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tmdbId"));
        this.tvdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tvdbId"));
        this.tvMazeId = NumberParser.parseInt(-1, StringParser.parse(obj, "tvMazeId"));
        this.tvRageId = NumberParser.parseInt(-1, StringParser.parse(obj, "tvRageId"));
        this.useSceneNumbering = BooleanParser.parse(false, StringParser.parse(obj, "useSceneNumbering"));
        this.year = NumberParser.parseInt(-1, StringParser.parse(obj, "year"));

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public @NotNull Instant added() {
        return added;
    }

    public @Nullable AddSeriesOptions addOptions() {
        return addOptions;
    }

    public @Nullable Duration airTime() {
        return airTime;
    }

    public @NotNull Set<@NotNull AlternateTitle> alternateTitles() {
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

    public @NotNull Set<@NotNull MediaCover> images() {
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

    public @NotNull SonarrMonitorType monitorNewItems() {
        return monitorNewItems;
    }

    public @Nullable String network() {
        return network;
    }

    public @Nullable Instant nextAiring() {
        return nextAiring;
    }

    public @NotNull Language originalLanguage() {
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

    public @NotNull QualityProfile qualityProfile() {
        return qualityProfile;
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

    public @NotNull Duration runtime() {
        return runtime;
    }

    public boolean seasonFolder() {
        return seasonFolder;
    }

    public @NotNull Set<@NotNull Season> seasons() {
        return seasons;
    }

    public @NotNull SonarrSeriesType seriesType() {
        return seriesType;
    }

    public @Nullable String sortTitle() {
        return sortTitle;
    }

    public @Nullable Statistics statistics() {
        return statistics;
    }

    public @NotNull SonarrSeriesStatus status() {
        return status;
    }

    public @NotNull Set<@NotNull Tag> tags() {
        return tags;
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
    public @NotNull Instant lastUpdated() {
        Instant r = Instant.EPOCH;

        AllEpisodes all = api.fetch(AllEpisodes.class, this.id);
        for (Episode e : all.items()) {
            Instant t = e.lastSearchTime();
            if (t != null && t.isAfter(r)) {
                r = t;
            }
        }

        return r;
    }

    @Override
    public @NotNull Instant lastSelected() {
        return this.lastSelected;
    }

    @Override
    public void lastSelectedNow() {
        this.lastSelected = Instant.now();

        if (ConfigVars.getBool(ConfigVars.USE_CACHE)) {
            CacheMeta meta = new CacheMeta(metaFile(id));
            meta.setSelected(this.lastSelected);
            meta.write();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Series series)) return false;
        return id == series.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Series{" +
                "id=" + id +
                ", added=" + added +
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
                ", qualityProfile=" + qualityProfile +
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
                '}';
    }

    public static class AddSeriesOptions {
        private final boolean ignoreEpisodesWithFiles;
        private final boolean ignoreEpisodesWithoutFiles;
        private final SonarrMonitorType monitor;
        private final boolean searchForCutoffUnmetEpisodes;
        private final boolean searchForMissingEpisodes;

        public AddSeriesOptions(@NotNull JSONObject obj) {
            this.ignoreEpisodesWithFiles = BooleanParser.parse(false, StringParser.parse(obj, "ignoreEpisodesWithFiles"));
            this.ignoreEpisodesWithoutFiles = BooleanParser.parse(false, StringParser.parse(obj, "ignoreEpisodesWithoutFiles"));
            this.monitor = SonarrMonitorType.parse(SonarrMonitorType.UNKNOWN, StringParser.parse(obj, "monitor"));
            this.searchForCutoffUnmetEpisodes = BooleanParser.parse(false, StringParser.parse(obj, "searchForCutoffUnmetEpisodes"));
            this.searchForMissingEpisodes = BooleanParser.parse(false, StringParser.parse(obj, "searchForMissingEpisodes"));
        }

        public boolean ignoreEpisodesWithFiles() {
            return ignoreEpisodesWithFiles;
        }

        public boolean ignoreEpisodesWithoutFiles() {
            return ignoreEpisodesWithoutFiles;
        }

        public @NotNull SonarrMonitorType monitor() {
            return monitor;
        }

        public boolean searchForCutoffUnmetEpisodes() {
            return searchForCutoffUnmetEpisodes;
        }

        public boolean searchForMissingEpisodes() {
            return searchForMissingEpisodes;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AddSeriesOptions that)) return false;
            return ignoreEpisodesWithFiles == that.ignoreEpisodesWithFiles && ignoreEpisodesWithoutFiles == that.ignoreEpisodesWithoutFiles && searchForCutoffUnmetEpisodes == that.searchForCutoffUnmetEpisodes && searchForMissingEpisodes == that.searchForMissingEpisodes && monitor == that.monitor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ignoreEpisodesWithFiles, ignoreEpisodesWithoutFiles, monitor, searchForCutoffUnmetEpisodes, searchForMissingEpisodes);
        }

        @Override
        public String toString() {
            return "AddSeriesOptions{" +
                    "ignoreEpisodesWithFiles=" + ignoreEpisodesWithFiles +
                    ", ignoreEpisodesWithoutFiles=" + ignoreEpisodesWithoutFiles +
                    ", monitor=" + monitor +
                    ", searchForCutoffUnmetEpisodes=" + searchForCutoffUnmetEpisodes +
                    ", searchForMissingEpisodes=" + searchForMissingEpisodes +
                    '}';
        }
    }

    public static class AlternateTitle {
        private final String comment;
        private final String sceneOrigin;
        private final int sceneSeasonNumber;
        private final int seasonNumber;
        private final String title;

        public AlternateTitle(@NotNull JSONObject obj) {
            this.comment = StringParser.parse(obj, "comment");
            this.sceneOrigin = StringParser.parse(obj, "sceneOrigin");
            this.sceneSeasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "sceneSeasonNumber"));
            this.seasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "seasonNumber"));
            this.title = StringParser.parse(obj, "title");
        }

        public @Nullable String comment() {
            return comment;
        }

        public @Nullable String sceneOrigin() {
            return sceneOrigin;
        }

        public int sceneSeasonNumber() {
            return sceneSeasonNumber;
        }

        public int seasonNumber() {
            return seasonNumber;
        }

        public @Nullable String title() {
            return title;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AlternateTitle that)) return false;
            return sceneSeasonNumber == that.sceneSeasonNumber && seasonNumber == that.seasonNumber && Objects.equals(sceneOrigin, that.sceneOrigin) && Objects.equals(title, that.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sceneOrigin, sceneSeasonNumber, seasonNumber, title);
        }

        @Override
        public String toString() {
            return "AlternateTitle{" +
                    "comment='" + comment + '\'' +
                    ", sceneOrigin='" + sceneOrigin + '\'' +
                    ", sceneSeasonNumber=" + sceneSeasonNumber +
                    ", seasonNumber=" + seasonNumber +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class Ratings {
        private final float value;
        private final int votes;

        public Ratings(@NotNull JSONObject obj) {
            this.value = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "value"));
            this.votes = NumberParser.parseInt(-1, StringParser.parse(obj, "votes"));
        }

        public float value() {
            return value;
        }

        public int votes() {
            return votes;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ratings ratings)) return false;
            return Double.compare(value, ratings.value) == 0 && votes == ratings.votes;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, votes);
        }

        @Override
        public String toString() {
            return "Ratings{" +
                    "value=" + value +
                    ", votes=" + votes +
                    '}';
        }
    }

    public static class Season {
        private final Set<@NotNull MediaCover> images = new HashSet<>();
        private final boolean monitored;
        private final int seasonNumber;
        private final Statistics statistics;

        public Season(@NotNull JSONObject obj) {
            JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
            if (images != null) {
                for (int i = 0; i < images.length(); i++) {
                    this.images.add(new MediaCover(images.getJSONObject(i)));
                }
            }

            this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
            this.seasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "seasonNumber"));
            this.statistics = obj.has("statistics") ? new Statistics(obj.getJSONObject("statistics")) : null;
        }

        public @NotNull Set<@NotNull MediaCover> images() {
            return images;
        }

        public boolean monitored() {
            return monitored;
        }

        public int seasonNumber() {
            return seasonNumber;
        }

        public @Nullable Statistics statistics() {
            return statistics;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Season season)) return false;
            return seasonNumber == season.seasonNumber;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(seasonNumber);
        }

        @Override
        public String toString() {
            return "Season{" +
                    "images=" + images +
                    ", monitored=" + monitored +
                    ", seasonNumber=" + seasonNumber +
                    ", statistics=" + statistics +
                    '}';
        }

        public static class Statistics {
            private final int episodeCount;
            private final int episodeFileCount;
            private final Instant nextAiring;
            private final float percentOfEpisodes;
            private final Instant previousAiring;
            private final Set<@NotNull String> releaseGroups = new HashSet<>();
            private final long sizeOnDisk;
            private final int totalEpisodeCount;

            public Statistics(@NotNull JSONObject obj) {
                this.episodeCount = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeCount"));
                this.episodeFileCount = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeFileCount"));
                this.nextAiring = InstantParser.parse(StringParser.parse(obj, "nextAiring"));
                this.percentOfEpisodes = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "percentOfEpisodes"));
                this.previousAiring = InstantParser.parse(StringParser.parse(obj, "previousAiring"));

                JSONArray releaseGroups = obj.has("releaseGroups") ? obj.getJSONArray("releaseGroups") : null;
                if (releaseGroups != null) {
                    for (int i = 0; i < releaseGroups.length(); i++) {
                        this.releaseGroups.add(releaseGroups.getString(i));
                    }
                }

                this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));
                this.totalEpisodeCount = NumberParser.parseInt(-1, StringParser.parse(obj, "totalEpisodeCount"));
            }

            public int episodeCount() {
                return episodeCount;
            }

            public int episodeFileCount() {
                return episodeFileCount;
            }

            public @Nullable Instant nextAiring() {
                return nextAiring;
            }

            public float percentOfEpisodes() {
                return percentOfEpisodes;
            }

            public @Nullable Instant previousAiring() {
                return previousAiring;
            }

            public @NotNull Set<@NotNull String> releaseGroups() {
                return releaseGroups;
            }

            public long sizeOnDisk() {
                return sizeOnDisk;
            }

            public int totalEpisodeCount() {
                return totalEpisodeCount;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Statistics that)) return false;
                return episodeCount == that.episodeCount && episodeFileCount == that.episodeFileCount && Float.compare(percentOfEpisodes, that.percentOfEpisodes) == 0 && sizeOnDisk == that.sizeOnDisk && totalEpisodeCount == that.totalEpisodeCount && Objects.equals(nextAiring, that.nextAiring) && Objects.equals(previousAiring, that.previousAiring) && Objects.equals(releaseGroups, that.releaseGroups);
            }

            @Override
            public int hashCode() {
                return Objects.hash(episodeCount, episodeFileCount, nextAiring, percentOfEpisodes, previousAiring, releaseGroups, sizeOnDisk, totalEpisodeCount);
            }

            @Override
            public String toString() {
                return "Statistics{" +
                        "episodeCount=" + episodeCount +
                        ", episodeFileCount=" + episodeFileCount +
                        ", nextAiring=" + nextAiring +
                        ", percentOfEpisodes=" + percentOfEpisodes +
                        ", previousAiring=" + previousAiring +
                        ", releaseGroups=" + releaseGroups +
                        ", sizeOnDisk=" + sizeOnDisk +
                        ", totalEpisodeCount=" + totalEpisodeCount +
                        '}';
            }
        }
    }

    public static class Statistics {
        private final int episodeCount;
        private final int episodeFileCount;
        private final float percentOfEpisodes;
        private final Set<@NotNull String> releaseGroups = new HashSet<>();
        private final int seasonCount;
        private final long sizeOnDisk;
        private final int totalEpisodeCount;

        public Statistics(@NotNull JSONObject obj) {
            this.episodeCount = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeCount"));
            this.episodeFileCount = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeFileCount"));
            this.percentOfEpisodes = NumberParser.parseFloat(-1, StringParser.parse(obj, "percentOfEpisodes"));

            JSONArray releaseGroups = obj.has("releaseGroups") ? obj.getJSONArray("releaseGroups") : null;
            if (releaseGroups != null) {
                for (int i = 0; i < releaseGroups.length(); i++) {
                    this.releaseGroups.add(releaseGroups.getString(i));
                }
            }

            this.seasonCount = NumberParser.parseInt(-1, StringParser.parse(obj, "seasonCount"));
            this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));
            this.totalEpisodeCount = NumberParser.parseInt(-1, StringParser.parse(obj, "totalEpisodeCount"));
        }

        public int episodeCount() {
            return episodeCount;
        }

        public int episodeFileCount() {
            return episodeFileCount;
        }

        public float percentOfEpisodes() {
            return percentOfEpisodes;
        }

        public @NotNull Set<@NotNull String> releaseGroups() {
            return releaseGroups;
        }

        public int seasonCount() {
            return seasonCount;
        }

        public long sizeOnDisk() {
            return sizeOnDisk;
        }

        public int totalEpisodeCount() {
            return totalEpisodeCount;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Statistics that)) return false;
            return episodeCount == that.episodeCount && episodeFileCount == that.episodeFileCount && Float.compare(percentOfEpisodes, that.percentOfEpisodes) == 0 && seasonCount == that.seasonCount && sizeOnDisk == that.sizeOnDisk && totalEpisodeCount == that.totalEpisodeCount && Objects.equals(releaseGroups, that.releaseGroups);
        }

        @Override
        public int hashCode() {
            return Objects.hash(episodeCount, episodeFileCount, percentOfEpisodes, releaseGroups, seasonCount, sizeOnDisk, totalEpisodeCount);
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "episodeCount=" + episodeCount +
                    ", episodeFileCount=" + episodeFileCount +
                    ", percentOfEpisodes=" + percentOfEpisodes +
                    ", releaseGroups=" + releaseGroups +
                    ", seasonCount=" + seasonCount +
                    ", sizeOnDisk=" + sizeOnDisk +
                    ", totalEpisodeCount=" + totalEpisodeCount +
                    '}';
        }
    }
}
