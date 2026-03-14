package me.egg82.fetcharr.web.model.lidarr;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Album extends AbstractAPIObject<Album> {
    public static Album UNKNOWN = new Album(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String title;
    private String disambiguation;
    private String overview;
    private Artist artist;
    private String foreignAlbumId;
    private boolean monitored;
    private boolean anyReleaseOk;
    private Profile profile;
    private Duration duration;
    private String albumType;
    private final Set<@NotNull String> secondaryTypes = new HashSet<>();
    private int mediumCount;
    private Ratings ratings;
    private Instant releaseDate;
    private final Set<@NotNull Release> releases = new HashSet<>();
    private final Set<@NotNull String> genres = new HashSet<>();
    private final Set<@NotNull Medium> media = new HashSet<>();
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private final Set<@NotNull Link> links = new HashSet<>();
    private Instant lastSearchTime;
    private Statistics statistics;
    private AddOptions addOptions;
    private String remoteCover;

    public Album(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/album/" + id);
        this.id = id;
    }

    @Override
    public Album fetch(@NotNull String apiKey) {
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

        this.disambiguation = StringParser.parse(obj, "disambiguation");
        this.overview = StringParser.parse(obj, "overview");

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "artistId"));
        this.artist = id >= 0 ? api.fetch(Artist.class, id, true) : null;

        this.foreignAlbumId = StringParser.parse(obj, "foreignAlbumId");
        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.anyReleaseOk = BooleanParser.parse(false, StringParser.parse(obj, "anyReleaseOk"));

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "profileId"));
        this.profile = id >= 0 ? api.fetch(Profile.class, id, true) : null;

        this.duration = DurationParser.parse(StringParser.parse(obj, "duration"));
        this.albumType = StringParser.parse(obj, "albumType");

        this.secondaryTypes.clear();
        JSONArray secondaryTypes = obj.has("secondaryTypes") ? obj.getJSONArray("secondaryTypes") : null;
        if (secondaryTypes != null) {
            for (int i = 0; i < secondaryTypes.length(); i++) {
                this.secondaryTypes.add(secondaryTypes.getString(i));
            }
        }

        this.mediumCount = NumberParser.parseInt(-1, StringParser.parse(obj, "mediumCount"));
        this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;
        this.releaseDate = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "releaseDate"));

        this.releases.clear();
        JSONArray releases = obj.has("releases") ? obj.getJSONArray("releases") : null;
        if (releases != null) {
            for (int i = 0; i < releases.length(); i++) {
                this.releases.add(new Release(releases.getJSONObject(i), this));
            }
        }

        this.genres.clear();
        JSONArray genres = obj.has("genres") ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        this.media.clear();
        JSONArray media = obj.has("media") ? obj.getJSONArray("media") : null;
        if (media != null) {
            for (int i = 0; i < media.length(); i++) {
                this.media.add(new Medium(media.getJSONObject(i)));
            }
        }

        id = obj.has("artist") ? NumberParser.parseInt(-1, StringParser.parse(obj.getJSONObject("artist"), "id")) : -1;
        this.artist = id >= 0 ? api.fetch(Artist.class, id, true) : null;

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.links.clear();
        JSONArray links = obj.has("links") ? obj.getJSONArray("links") : null;
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                this.links.add(new Link(links.getJSONObject(i)));
            }
        }

        this.lastSearchTime = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "lastSearchTime"));
        this.statistics = obj.has("statistics") ? new Statistics(obj.getJSONObject("statistics")) : null;
        this.addOptions = obj.has("addOptions") ? new AddOptions(obj.getJSONObject("addOptions")) : null;
        this.remoteCover = StringParser.parse(obj, "remoteCover");

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable Artist artist() {
        return artist;
    }

    public @Nullable String foreignAlbumId() {
        return foreignAlbumId;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean anyReleaseOk() {
        return anyReleaseOk;
    }

    public @Nullable Profile profile() {
        return profile;
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public @Nullable String albumType() {
        return albumType;
    }

    public @NotNull Set<@NotNull String> secondaryTypes() {
        return secondaryTypes;
    }

    public int mediumCount() {
        return mediumCount;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @NotNull Instant releaseDate() {
        return releaseDate;
    }

    public @NotNull Set<@NotNull Release> releases() {
        return releases;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public @NotNull Set<@NotNull Medium> media() {
        return media;
    }

    public @NotNull Set<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull Set<@NotNull Link> links() {
        return links;
    }

    public @NotNull Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @Nullable Statistics statistics() {
        return statistics;
    }

    public @Nullable AddOptions addOptions() {
        return addOptions;
    }

    public @Nullable String remoteCover() {
        return remoteCover;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Album album)) return false;
        return id == album.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", overview='" + overview + '\'' +
                ", artist=" + artist +
                ", foreignAlbumId='" + foreignAlbumId + '\'' +
                ", monitored=" + monitored +
                ", anyReleaseOk=" + anyReleaseOk +
                ", profile=" + profile +
                ", duration=" + duration +
                ", albumType='" + albumType + '\'' +
                ", secondaryTypes=" + secondaryTypes +
                ", mediumCount=" + mediumCount +
                ", ratings=" + ratings +
                ", releaseDate=" + releaseDate +
                ", releases=" + releases +
                ", genres=" + genres +
                ", media=" + media +
                ", images=" + images +
                ", links=" + links +
                ", lastSearchTime=" + lastSearchTime +
                ", statistics=" + statistics +
                ", addOptions=" + addOptions +
                ", remoteCover='" + remoteCover + '\'' +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class Release {
        private final int id;

        private final Album album;
        private final String foreignReleaseId;
        private final String title;
        private final String status;
        private final Duration duration;
        private final int trackCount;
        private final Set<@NotNull Medium> media = new HashSet<>();
        private final int mediumCount;
        private final String disambiguation;
        private final Set<@NotNull String> country = new HashSet<>();
        private final Set<@NotNull String> label = new HashSet<>();
        private final String format;
        private final boolean monitored;

        public Release(@NotNull JSONObject obj, @NotNull Album album) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.album = album;
            this.foreignReleaseId = StringParser.parse(obj, "foreignReleaseId");
            this.title = StringParser.parse(obj, "title");
            this.status = StringParser.parse(obj, "status");
            this.duration = DurationParser.parse(StringParser.parse(obj, "duration"));
            this.trackCount = NumberParser.parseInt(-1, StringParser.parse(obj, "trackCount"));

            JSONArray media = obj.has("media") ? obj.getJSONArray("media") : null;
            if (media != null) {
                for (int i = 0; i < media.length(); i++) {
                    this.media.add(new Medium(media.getJSONObject(i)));
                }
            }

            this.mediumCount = NumberParser.parseInt(-1, StringParser.parse(obj, "mediumCount"));
            this.disambiguation = StringParser.parse(obj, "disambiguation");

            JSONArray country = obj.has("country") ? obj.getJSONArray("country") : null;
            if (country != null) {
                for (int i = 0; i < country.length(); i++) {
                    this.country.add(country.getString(i));
                }
            }

            JSONArray label = obj.has("label") ? obj.getJSONArray("label") : null;
            if (label != null) {
                for (int i = 0; i < label.length(); i++) {
                    this.label.add(label.getString(i));
                }
            }

            this.format = StringParser.parse(obj, "format");
            this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        }

        public int id() {
            return id;
        }

        public @NotNull Album album() {
            return album;
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

        public @NotNull Duration duration() {
            return duration;
        }

        public int trackCount() {
            return trackCount;
        }

        public @NotNull Set<@NotNull Medium> media() {
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
            if (!(o instanceof Release release)) return false;
            return id == release.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Release{" +
                    "id=" + id +
                    ", album=" + album +
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
                    '}';
        }
    }

    public static class Medium {
        private final int mediumNumber;
        private final String mediumName;
        private final String mediumFormat;

        public Medium(@NotNull JSONObject obj) {
            this.mediumNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "mediumNumber"));
            this.mediumName = StringParser.parse(obj, "mediumName");
            this.mediumFormat = StringParser.parse(obj, "mediumFormat");
        }

        public int mediumNumber() {
            return mediumNumber;
        }

        public @Nullable String mediumName() {
            return mediumName;
        }

        public @Nullable String mediumFormat() {
            return mediumFormat;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Medium medium)) return false;
            return mediumNumber == medium.mediumNumber && Objects.equals(mediumName, medium.mediumName) && Objects.equals(mediumFormat, medium.mediumFormat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mediumNumber, mediumName, mediumFormat);
        }

        @Override
        public String toString() {
            return "Medium{" +
                    "mediumNumber=" + mediumNumber +
                    ", mediumName='" + mediumName + '\'' +
                    ", mediumFormat='" + mediumFormat + '\'' +
                    '}';
        }
    }

    public static class Statistics {
        private final int trackFileCount;
        private final int trackCount;
        private final int totalTrackCount;
        private final long sizeOnDisk;
        private final float percentOfTracks;

        public Statistics(@NotNull JSONObject obj) {
            this.trackFileCount = NumberParser.parseInt(-1, StringParser.parse(obj, "trackFileCount"));
            this.trackCount = NumberParser.parseInt(-1, StringParser.parse(obj, "trackCount"));
            this.totalTrackCount = NumberParser.parseInt(-1, StringParser.parse(obj, "totalTrackCount"));
            this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));
            this.percentOfTracks = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "percentOfTracks"));
        }

        public int trackFileCount() {
            return trackFileCount;
        }

        public int trackCount() {
            return trackCount;
        }

        public int totalTrackCount() {
            return totalTrackCount;
        }

        public long sizeOnDisk() {
            return sizeOnDisk;
        }

        public float percentOfTracks() {
            return percentOfTracks;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Statistics that)) return false;
            return trackFileCount == that.trackFileCount && trackCount == that.trackCount && totalTrackCount == that.totalTrackCount && sizeOnDisk == that.sizeOnDisk && Float.compare(percentOfTracks, that.percentOfTracks) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(trackFileCount, trackCount, totalTrackCount, sizeOnDisk, percentOfTracks);
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "trackFileCount=" + trackFileCount +
                    ", trackCount=" + trackCount +
                    ", totalTrackCount=" + totalTrackCount +
                    ", sizeOnDisk=" + sizeOnDisk +
                    ", percentOfTracks=" + percentOfTracks +
                    '}';
        }
    }

    public static class AddOptions {
        private final AlbumAddType addType;
        private final boolean searchForNewAlbum;

        public AddOptions(@NotNull JSONObject obj) {
            this.addType = AlbumAddType.parse(AlbumAddType.MANUAL, StringParser.parse(obj, "addType"));
            this.searchForNewAlbum = BooleanParser.parse(false, StringParser.parse(obj, "searchForNewAlbum"));
        }

        public @NotNull AlbumAddType addType() {
            return addType;
        }

        public boolean searchForNewAlbum() {
            return searchForNewAlbum;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AddOptions that)) return false;
            return searchForNewAlbum == that.searchForNewAlbum && addType == that.addType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(addType, searchForNewAlbum);
        }

        @Override
        public String toString() {
            return "AddOptions{" +
                    "addType=" + addType +
                    ", searchForNewAlbum=" + searchForNewAlbum +
                    '}';
        }
    }
}
