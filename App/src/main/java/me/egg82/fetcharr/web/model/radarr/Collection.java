package me.egg82.fetcharr.web.model.radarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import me.egg82.fetcharr.web.model.common.MediaCover;
import me.egg82.fetcharr.web.model.common.QualityProfile;
import me.egg82.fetcharr.web.model.common.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Collection extends AbstractAPIObject<Collection> {
    public static final Collection UNKNOWN = new Collection(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String title;
    private String sortTitle;
    private int tmdbId;
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private String overview;
    private boolean monitored;
    private File rootFolderPath;
    private QualityProfile qualityProfile;
    private boolean searchOnAdd;
    private RadarrMovieStatus minimumAvailability;
    private final Set<@NotNull CollectionItem> movies = new HashSet<>();
    private int missingMovies;
    private final Set<@NotNull Tag> tags = new HashSet<>();

    public Collection(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/collection/" + id);
        this.id = id;
    }

    @Override
    public Collection fetch(@NotNull String apiKey) {
        if (this.id < 0 || !this.fetching.compareAndSet(false, true)) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile(id));
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.LONG_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile(id);
            try {
                parse(data.read());
                if (this.title != null) {
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

        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.LONG_CACHE_TIME);
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

        this.sortTitle = StringParser.parse(obj, "sortTitle");
        this.tmdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tmdbId"));

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.overview = StringParser.parse(obj, "overview");
        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.rootFolderPath = FileParser.parse(StringParser.parse(obj, "rootFolderPath"));

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "qualityProfile"));
        this.qualityProfile = id >= 0 ? api.fetch(QualityProfile.class, id, false) : QualityProfile.UNKNOWN;

        this.searchOnAdd = BooleanParser.parse(false, StringParser.parse(obj, "searchOnAdd"));
        this.minimumAvailability = RadarrMovieStatus.parse(RadarrMovieStatus.ANNOUNCED, StringParser.parse(obj, "minimumAvailability"));

        this.movies.clear();
        JSONArray movies = obj.has("movies") ? obj.getJSONArray("movies") : null;
        if (movies != null) {
            for (int i = 0; i < movies.length(); i++) {
                this.movies.add(new CollectionItem(movies.getJSONObject(i)));
            }
        }

        this.missingMovies = NumberParser.parseInt(-1, StringParser.parse(obj, "missingMovies"));

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

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String sortTitle() {
        return sortTitle;
    }

    public int tmdbId() {
        return tmdbId;
    }

    public @NotNull Set<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable String overview() {
        return overview;
    }

    public boolean monitored() {
        return monitored;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @NotNull QualityProfile qualityProfile() {
        return qualityProfile;
    }

    public boolean searchOnAdd() {
        return searchOnAdd;
    }

    public @NotNull RadarrMovieStatus minimumAvailability() {
        return minimumAvailability;
    }

    public @NotNull Set<@NotNull CollectionItem> movies() {
        return movies;
    }

    public int missingMovies() {
        return missingMovies;
    }

    public @NotNull Set<@NotNull Tag> tags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Collection that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sortTitle='" + sortTitle + '\'' +
                ", tmdbId=" + tmdbId +
                ", images=" + images +
                ", overview='" + overview + '\'' +
                ", monitored=" + monitored +
                ", rootFolderPath=" + rootFolderPath +
                ", qualityProfile=" + qualityProfile +
                ", searchOnAdd=" + searchOnAdd +
                ", minimumAvailability=" + minimumAvailability +
                ", movies=" + movies +
                ", missingMovies=" + missingMovies +
                ", tags=" + tags +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class CollectionItem {
        private final int tmdbId;
        private final String imdbId;
        private final String title;
        private final String cleanTitle;
        private final String sortTitle;
        private final RadarrMovieStatus status;
        private final String overview;
        private final Duration runtime;
        private final Set<@NotNull MediaCover> images = new HashSet<>();
        private final int year;
        private final Ratings ratings;
        private final Set<@NotNull String> genres = new HashSet<>();
        private final File folder;
        private final boolean isExisting;
        private final boolean isExcluded;

        public CollectionItem(@NotNull JSONObject obj) {
            this.tmdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tmdbId"));
            this.imdbId = StringParser.parse(obj, "imdbId");
            this.title = StringParser.parse(obj, "title");
            this.cleanTitle = StringParser.parse(obj, "cleanTitle");
            this.sortTitle = StringParser.parse(obj, "sortTitle");
            this.status = RadarrMovieStatus.parse(RadarrMovieStatus.ANNOUNCED, StringParser.parse(obj, "status"));
            this.overview = StringParser.parse(obj, "overview");
            this.runtime = DurationParser.parse(StringParser.parse(obj, "runtime"));

            JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
            if (images != null) {
                for (int i = 0; i < images.length(); i++) {
                    this.images.add(new MediaCover(images.getJSONObject(i)));
                }
            }

            this.year = NumberParser.parseInt(-1, StringParser.parse(obj, "year"));
            this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;

            JSONArray genres = obj.has("genres") ? obj.getJSONArray("genres") : null;
            if (genres != null) {
                for (int i = 0; i < genres.length(); i++) {
                    this.genres.add(genres.getString(i));
                }
            }

            this.folder = FileParser.parse(StringParser.parse(obj, "folder"));
            this.isExisting = BooleanParser.parse(false, StringParser.parse(obj, "isExisting"));
            this.isExcluded = BooleanParser.parse(false, StringParser.parse(obj, "isExcluded"));
        }

        public int tmdbId() {
            return tmdbId;
        }

        public @Nullable String imdbId() {
            return imdbId;
        }

        public @Nullable String title() {
            return title;
        }

        public @Nullable String cleanTitle() {
            return cleanTitle;
        }

        public @Nullable String sortTitle() {
            return sortTitle;
        }

        public @NotNull RadarrMovieStatus status() {
            return status;
        }

        public @Nullable String overview() {
            return overview;
        }

        public @Nullable Duration runtime() {
            return runtime;
        }

        public @NotNull Set<@NotNull MediaCover> images() {
            return images;
        }

        public int year() {
            return year;
        }

        public @Nullable Ratings ratings() {
            return ratings;
        }

        public @NotNull Set<@NotNull String> genres() {
            return genres;
        }

        public @Nullable File folder() {
            return folder;
        }

        public boolean isExisting() {
            return isExisting;
        }

        public boolean isExcluded() {
            return isExcluded;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CollectionItem that)) return false;
            return tmdbId == that.tmdbId && Objects.equals(imdbId, that.imdbId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tmdbId, imdbId);
        }

        @Override
        public String toString() {
            return "CollectionItem{" +
                    "tmdbId=" + tmdbId +
                    ", imdbId='" + imdbId + '\'' +
                    ", title='" + title + '\'' +
                    ", cleanTitle='" + cleanTitle + '\'' +
                    ", sortTitle='" + sortTitle + '\'' +
                    ", status=" + status +
                    ", overview='" + overview + '\'' +
                    ", runtime=" + runtime +
                    ", images=" + images +
                    ", year=" + year +
                    ", ratings=" + ratings +
                    ", genres=" + genres +
                    ", folder=" + folder +
                    ", isExisting=" + isExisting +
                    ", isExcluded=" + isExcluded +
                    '}';
        }
    }
}
