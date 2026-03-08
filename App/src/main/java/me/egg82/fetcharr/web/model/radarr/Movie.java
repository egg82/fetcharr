package me.egg82.fetcharr.web.model.radarr;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Movie extends AbstractAPIObject<Movie> implements Weighted {
    public static Movie UNKNOWN = new Movie(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String title;
    private String originalTitle;
    private Language originalLanguage;
    private final Set<@NotNull AlternativeTitle> alternateTitles = new HashSet<>();
    private int secondaryYear;
    private int secondaryYearSourceId;
    private String sortTitle;
    private long sizeOnDisk;
    private RadarrMovieStatus status;
    private String overview;
    private Instant inCinemas;
    private Instant physicalRelease;
    private Instant digitalRelease;
    private Instant releaseDate;
    private String physicalReleaseNote;
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private String website;
    private String remotePoster;
    private int year;
    private String youTubeTrailerId;
    private String studio;
    private File path;
    private QualityProfile qualityProfile;
    private boolean hasFile;
    private MovieFile movieFile;
    private boolean monitored;
    private RadarrMovieStatus minimumAvailability;
    private boolean isAvailable;
    private String folderName;
    private Duration runtime;
    private String cleanTitle;
    private String imdbId;
    private int tmdbId;
    private String titleSlug;
    private File rootFolderPath;
    private File folder;
    private String certification;
    private final Set<@NotNull String> genres = new HashSet<>();
    private final Set<@NotNull String> keywords = new HashSet<>();
    private final Set<@NotNull Tag> tags = new HashSet<>();
    private Instant added;
    private AddOptions addOptions;
    private Ratings ratings;
    private Collection collection;
    private float popularity;
    private Instant lastSearchTime;
    private Statistics statistics;

    private Instant lastSelected = Instant.EPOCH;

    public Movie(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/movie/" + id);
        this.id = id;
    }

    public Movie(@NotNull ArrAPI api, int id, @NotNull JSONObject obj) {
        this(api, id);

        CacheMeta meta = new CacheMeta(metaFile(id));

        JsonNode node = new JsonNode(obj.toString());
        try {
            parse(node);
        } catch (Exception ex) {
            logger.warn("Could not read data from {}", obj, ex);
            return;
        }

        this.fetched = Instant.now();
        try {
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
    }

    @Override
    public Movie fetch(@NotNull String apiKey) {
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

        this.originalTitle = StringParser.parse(obj, "originalTitle");

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "originalLanguage"));
        this.originalLanguage = id >= 0 ? api.fetch(Language.class, id, false) : Language.UNKNOWN;

        this.alternateTitles.clear();
        JSONArray alternateTitles = obj.has("alternateTitles") ? obj.getJSONArray("alternateTitles") : null;
        if (alternateTitles != null) {
            for (int i = 0; i < alternateTitles.length(); i++) {
                this.alternateTitles.add(new AlternativeTitle(alternateTitles.getJSONObject(i)));
            }
        }

        this.secondaryYear = NumberParser.parseInt(-1, StringParser.parse(obj, "secondaryYear"));
        this.secondaryYearSourceId = NumberParser.parseInt(-1, StringParser.parse(obj, "secondaryYearSourceId"));
        this.sortTitle = StringParser.parse(obj, "sortTitle");
        this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));
        this.status = RadarrMovieStatus.parse(RadarrMovieStatus.DELETED, StringParser.parse(obj, "status"));
        this.overview = StringParser.parse(obj, "overview");
        this.inCinemas = InstantParser.parse(StringParser.parse(obj, "inCinemas"));
        this.physicalRelease = InstantParser.parse(StringParser.parse(obj, "physicalRelease"));
        this.digitalRelease = InstantParser.parse(StringParser.parse(obj, "digitalRelease"));
        this.releaseDate = InstantParser.parse(StringParser.parse(obj, "releaseDate"));
        this.physicalReleaseNote = StringParser.parse(obj, "physicalReleaseNote");

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.website = StringParser.parse(obj, "website");
        this.remotePoster = StringParser.parse(obj, "remotePoster");
        this.year = NumberParser.parseInt(-1, StringParser.parse(obj, "year"));
        this.youTubeTrailerId = StringParser.parse(obj, "youTubeTrailerId");
        this.studio = StringParser.parse(obj, "studio");
        this.path = FileParser.parse(StringParser.parse(obj, "path"));

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "qualityProfile"));
        this.qualityProfile = id >= 0 ? api.fetch(QualityProfile.class, id, false) : QualityProfile.UNKNOWN;

        this.hasFile = BooleanParser.parse(false, StringParser.parse(obj, "hasFile"));

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "movieFileId"));
        JSONObject o = obj.has("movieFile") ? obj.getJSONObject("movieFile") : null;
        this.movieFile = id >= 0 && o != null ? new MovieFile(api, id, o) : MovieFile.UNKNOWN;

        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.minimumAvailability = RadarrMovieStatus.parse(RadarrMovieStatus.ANNOUNCED, StringParser.parse(obj, "minimumAvailability"));
        this.isAvailable = BooleanParser.parse(false, StringParser.parse(obj, "isAvailable"));
        this.folderName = StringParser.parse(obj, "folderName");
        this.runtime = DurationParser.parse(StringParser.parse(obj, "runtime"));
        this.cleanTitle = StringParser.parse(obj, "cleanTitle");
        this.imdbId = StringParser.parse(obj, "imdbId");
        this.tmdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tmdbId"));
        this.titleSlug = StringParser.parse(obj, "titleSlug");
        this.rootFolderPath = FileParser.parse(StringParser.parse(obj, "rootFolderPath"));
        this.folder = FileParser.parse(StringParser.parse(obj, "folder"));
        this.certification = StringParser.parse(obj, "certification");

        this.genres.clear();
        JSONArray genres = obj.has("genres") ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        this.keywords.clear();
        JSONArray keywords = obj.has("keywords") ? obj.getJSONArray("keywords") : null;
        if (keywords != null) {
            for (int i = 0; i < keywords.length(); i++) {
                this.keywords.add(keywords.getString(i));
            }
        }

        this.tags.clear();
        JSONArray tags = obj.has("tags") ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                id = NumberParser.parseInt(-1, tags.getString(i));
                if (id >= 0) {
                    this.tags.add(api.fetch(Tag.class, id, false));
                }
            }
        }

        this.addOptions = obj.has("addOptions") ? new AddOptions(obj.getJSONObject("addOptions")) : null;
        this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "collection"));
        this.collection = id >= 0 ? api.fetch(Collection.class, id, false) : Collection.UNKNOWN;

        this.popularity = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "popularity"));
        this.lastSearchTime = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "lastSearchTime"));
        this.statistics = obj.has("statistics") ? new Statistics(obj.getJSONObject("statistics")) : null;

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String originalTitle() {
        return originalTitle;
    }

    public @Nullable Language originalLanguage() {
        return originalLanguage;
    }

    public @NotNull Set<@NotNull AlternativeTitle> alternateTitles() {
        return alternateTitles;
    }

    public int secondaryYear() {
        return secondaryYear;
    }

    public int secondaryYearSourceId() {
        return secondaryYearSourceId;
    }

    public @Nullable String sortTitle() {
        return sortTitle;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public @NotNull RadarrMovieStatus status() {
        return status;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable Instant inCinemas() {
        return inCinemas;
    }

    public @Nullable Instant physicalRelease() {
        return physicalRelease;
    }

    public @Nullable Instant digitalRelease() {
        return digitalRelease;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public @Nullable String physicalReleaseNote() {
        return physicalReleaseNote;
    }

    public @NotNull Set<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable String website() {
        return website;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public int year() {
        return year;
    }

    public @Nullable String youTubeTrailerId() {
        return youTubeTrailerId;
    }

    public @Nullable String studio() {
        return studio;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityProfile qualityProfile() {
        return qualityProfile;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public @NotNull MovieFile movieFile() {
        return movieFile;
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull RadarrMovieStatus minimumAvailability() {
        return minimumAvailability;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public @Nullable String folderName() {
        return folderName;
    }

    public @Nullable Duration runtime() {
        return runtime;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public @Nullable String imdbId() {
        return imdbId;
    }

    public int tmdbId() {
        return tmdbId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable File folder() {
        return folder;
    }

    public @Nullable String certification() {
        return certification;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public @NotNull Set<@NotNull String> keywords() {
        return keywords;
    }

    public @NotNull Set<@NotNull Tag> tags() {
        return tags;
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddOptions addOptions() {
        return addOptions;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable Collection collection() {
        return collection;
    }

    public float popularity() {
        return popularity;
    }

    public @NotNull Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @Nullable Statistics statistics() {
        return statistics;
    }

    @Override
    public @NotNull Instant lastUpdated() {
        return this.lastSearchTime != null ? this.lastSearchTime : Instant.EPOCH;
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
        if (!(o instanceof Movie movie)) return false;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", originalLanguage=" + originalLanguage +
                ", alternateTitles=" + alternateTitles +
                ", secondaryYear=" + secondaryYear +
                ", secondaryYearSourceId=" + secondaryYearSourceId +
                ", sortTitle='" + sortTitle + '\'' +
                ", sizeOnDisk=" + sizeOnDisk +
                ", status=" + status +
                ", overview='" + overview + '\'' +
                ", inCinemas=" + inCinemas +
                ", physicalRelease=" + physicalRelease +
                ", digitalRelease=" + digitalRelease +
                ", releaseDate=" + releaseDate +
                ", physicalReleaseNote='" + physicalReleaseNote + '\'' +
                ", images=" + images +
                ", website='" + website + '\'' +
                ", remotePoster='" + remotePoster + '\'' +
                ", year=" + year +
                ", youTubeTrailerId='" + youTubeTrailerId + '\'' +
                ", studio='" + studio + '\'' +
                ", path='" + path + '\'' +
                ", qualityProfile=" + qualityProfile +
                ", hasFile=" + hasFile +
                ", movieFile=" + movieFile +
                ", monitored=" + monitored +
                ", minimumAvailability=" + minimumAvailability +
                ", isAvailable=" + isAvailable +
                ", folderName='" + folderName + '\'' +
                ", runtime=" + runtime +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", tmdbId=" + tmdbId +
                ", titleSlug='" + titleSlug + '\'' +
                ", rootFolderPath=" + rootFolderPath +
                ", folder=" + folder +
                ", certification='" + certification + '\'' +
                ", genres=" + genres +
                ", keywords=" + keywords +
                ", tags=" + tags +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", ratings=" + ratings +
                ", collection=" + collection +
                ", popularity=" + popularity +
                ", lastSearchTime=" + lastSearchTime +
                ", statistics=" + statistics +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public class AlternativeTitle {
        private final int id;
        private final TitleSourceType sourceType;
        private final Metadata movieMetadata;
        private final String title;
        private final String cleanTitle;

        public AlternativeTitle(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.sourceType = TitleSourceType.parse(TitleSourceType.MAPPINGS, StringParser.parse(obj, "sourceType"));

            int id = NumberParser.parseInt(-1, StringParser.parse(obj, "movieMetadataId"));
            this.movieMetadata = id >= 0 ? api.fetch(Metadata.class, id, true) : null;

            this.title = StringParser.parse(obj, "title");
            this.cleanTitle = StringParser.parse(obj, "cleanTitle");
        }

        public int id() {
            return id;
        }

        public @NotNull TitleSourceType sourceType() {
            return sourceType;
        }

        public @Nullable Metadata movieMetadata() {
            return movieMetadata;
        }

        public @Nullable String title() {
            return title;
        }

        public @Nullable String cleanTitle() {
            return cleanTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AlternativeTitle that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "AlternativeTitle{" +
                    "id=" + id +
                    ", sourceType=" + sourceType +
                    ", movieMetadata=" + movieMetadata +
                    ", title='" + title + '\'' +
                    ", cleanTitle='" + cleanTitle + '\'' +
                    '}';
        }
    }

    public static class AddOptions {
        private final boolean ignoreEpisodesWithFiles;
        private final boolean ignoreEpisodesWithoutFiles;
        private final RadarrMonitorType monitor;
        private final boolean searchForMovie;
        private final AddMethod addMethod;

        public AddOptions(@NotNull JSONObject obj) {
            this.ignoreEpisodesWithFiles = BooleanParser.parse(false, StringParser.parse(obj, "ignoreEpisodesWithFiles"));
            this.ignoreEpisodesWithoutFiles = BooleanParser.parse(false, StringParser.parse(obj, "ignoreEpisodesWithoutFiles"));
            this.monitor = RadarrMonitorType.parse(RadarrMonitorType.NONE, StringParser.parse(obj, "monitor"));
            this.searchForMovie = BooleanParser.parse(false, StringParser.parse(obj, "searchForMovie"));
            this.addMethod = AddMethod.parse(AddMethod.MANUAL, StringParser.parse(obj, "addMethod"));
        }

        public boolean ignoreEpisodesWithFiles() {
            return ignoreEpisodesWithFiles;
        }

        public boolean ignoreEpisodesWithoutFiles() {
            return ignoreEpisodesWithoutFiles;
        }

        public @NotNull RadarrMonitorType monitor() {
            return monitor;
        }

        public boolean searchForMovie() {
            return searchForMovie;
        }

        public @NotNull AddMethod addMethod() {
            return addMethod;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AddOptions that)) return false;
            return ignoreEpisodesWithFiles == that.ignoreEpisodesWithFiles && ignoreEpisodesWithoutFiles == that.ignoreEpisodesWithoutFiles && searchForMovie == that.searchForMovie && monitor == that.monitor && addMethod == that.addMethod;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ignoreEpisodesWithFiles, ignoreEpisodesWithoutFiles, monitor, searchForMovie, addMethod);
        }

        @Override
        public String toString() {
            return "AddOptions{" +
                    "ignoreEpisodesWithFiles=" + ignoreEpisodesWithFiles +
                    ", ignoreEpisodesWithoutFiles=" + ignoreEpisodesWithoutFiles +
                    ", monitor=" + monitor +
                    ", searchForMovie=" + searchForMovie +
                    ", addMethod=" + addMethod +
                    '}';
        }
    }

    public static class Statistics {
        private final int movieFileCount;
        private final long sizeOnDisk;
        private final Set<@NotNull String> releaseGroups = new HashSet<>();

        public Statistics(@NotNull JSONObject obj) {
            this.movieFileCount = NumberParser.parseInt(-1, StringParser.parse(obj, "movieFileCount"));
            this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));

            String releaseGroups = StringParser.parse(obj, "releaseGroups");
            if (releaseGroups != null) {
                this.releaseGroups.addAll(Arrays.asList(releaseGroups.trim().split(",")));
            }
        }

        public int movieFileCount() {
            return movieFileCount;
        }

        public long sizeOnDisk() {
            return sizeOnDisk;
        }

        public @NotNull Set<@NotNull String> releaseGroups() {
            return releaseGroups;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Statistics that)) return false;
            return movieFileCount == that.movieFileCount && sizeOnDisk == that.sizeOnDisk && Objects.equals(releaseGroups, that.releaseGroups);
        }

        @Override
        public int hashCode() {
            return Objects.hash(movieFileCount, sizeOnDisk, releaseGroups);
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "movieFileCount=" + movieFileCount +
                    ", sizeOnDisk=" + sizeOnDisk +
                    ", releaseGroups=" + releaseGroups +
                    '}';
        }
    }
}
