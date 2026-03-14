package me.egg82.fetcharr.web.model.lidarr;

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
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Artist extends AbstractAPIObject<Artist> implements Weighted {
    public static final Artist UNKNOWN = new Artist(ArrAPI.UNKNOWN, -1);

    private final int id;

    private LidarrArtistStatus status;
    private boolean ended;
    private String artistName;
    private String foreignArtistId;
    private String mbId;
    private int tadbId;
    private int discogsId;
    private String allMusicId;
    private String overview;
    private String artistType;
    private String disambiguation;
    private final Set<@NotNull Link> links = new HashSet<>();
    private Album nextAlbum;
    private Album lastAlbum;
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private final Set<@NotNull Member> members = new HashSet<>();
    private String remotePoster;
    private File path;
    private QualityProfile qualityProfile;
    private MetadataProfile metadataProfile;
    private boolean monitored;
    private LidarrMonitorType monitorNewItems;
    private File rootFolderPath;
    private File folder;
    private final Set<@NotNull String> genres = new HashSet<>();
    private String cleanName;
    private String sortName;
    private final Set<@NotNull Tag> tags = new HashSet<>();
    private Instant added;
    private AddOptions addOptions;
    private Ratings ratings;
    private Statistics statistics;

    private Instant lastSelected = Instant.EPOCH;

    public Artist(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/artist/" + id);
        this.id = id;
    }

    public Artist(@NotNull ArrAPI api, int id, @NotNull JSONObject obj) {
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
    public Artist fetch(@NotNull String apiKey) {
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
                if (this.artistName != null && !this.artistName.isBlank()) {
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
        AllAlbums all = api.fetch(AllAlbums.class, this.id, false);
        for (Album a : all.items()) {
            a.invalidate();
        }

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

        this.status = LidarrArtistStatus.parse(LidarrArtistStatus.DELETED, StringParser.parse(obj, "status"));
        this.ended = BooleanParser.parse(false, StringParser.parse(obj, "ended"));
        this.foreignArtistId = StringParser.parse(obj, "foreignArtistId");
        this.mbId = StringParser.parse(obj, "mbId");
        this.tadbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tadbId"));
        this.discogsId = NumberParser.parseInt(-1, StringParser.parse(obj, "discogsId"));
        this.allMusicId = StringParser.parse(obj, "allMusicId");
        this.overview = StringParser.parse(obj, "overview");
        this.artistType = StringParser.parse(obj, "artistType");
        this.disambiguation = StringParser.parse(obj, "disambiguation");

        this.links.clear();
        JSONArray links = obj.has("links") ? obj.getJSONArray("links") : null;
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                this.links.add(new Link(links.getJSONObject(i)));
            }
        }

        this.nextAlbum = obj.has("nextAlbum") ? new Album(api, NumberParser.parseInt(-1, StringParser.parse(obj.getJSONObject("nextAlbum"), "id")), obj.getJSONObject("nextAlbum")) : null;
        this.lastAlbum = obj.has("lastAlbum") ? new Album(api, NumberParser.parseInt(-1, StringParser.parse(obj.getJSONObject("nextAlbum"), "id")), obj.getJSONObject("lastAlbum")) : null;

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.remotePoster = StringParser.parse(obj, "remotePoster");
        this.path = FileParser.parse(StringParser.parse(obj, "path"));

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "qualityProfileId"));
        this.qualityProfile = id >= 0 ? api.fetch(QualityProfile.class, id, false) : null;

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "metadataProfileId"));
        this.metadataProfile = id >= 0 ? api.fetch(MetadataProfile.class, id, false) : null;

        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.monitorNewItems = LidarrMonitorType.parse(LidarrMonitorType.UNKNOWN, StringParser.parse(obj, "monitorNewItems"));
        this.rootFolderPath = FileParser.parse(StringParser.parse(obj, "rootFolderPath"));
        this.folder = FileParser.parse(StringParser.parse(obj, "folder"));

        this.genres.clear();
        JSONArray genres = obj.has("genres") ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        this.cleanName = StringParser.parse(obj, "cleanName");
        this.sortName = StringParser.parse(obj, "sortName");

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

        this.added = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "added"));
        this.addOptions = obj.has("addOptions") ? new AddOptions(obj.getJSONObject("addOptions")) : null;
        this.ratings = obj.has("ratings") ? new Ratings(obj.getJSONObject("ratings")) : null;
        this.statistics = obj.has("statistics") ? new Statistics(obj.getJSONObject("statistics")) : null;

        this.artistName = StringParser.parse(obj, "artistName");
    }

    public int id() {
        return id;
    }

    public @NotNull LidarrArtistStatus status() {
        return status;
    }

    public boolean ended() {
        return ended;
    }

    public @Nullable String artistName() {
        return artistName;
    }

    public @Nullable String foreignArtistId() {
        return foreignArtistId;
    }

    public @Nullable String mbId() {
        return mbId;
    }

    public int tadbId() {
        return tadbId;
    }

    public int discogsId() {
        return discogsId;
    }

    public @Nullable String allMusicId() {
        return allMusicId;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable String artistType() {
        return artistType;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @NotNull Set<@NotNull Link> links() {
        return links;
    }

    public @Nullable Album nextAlbum() {
        return nextAlbum;
    }

    public @Nullable Album lastAlbum() {
        return lastAlbum;
    }

    public @NotNull Set<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull Set<@NotNull Member> members() {
        return members;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityProfile qualityProfile() {
        return qualityProfile;
    }

    public @Nullable MetadataProfile metadataProfile() {
        return metadataProfile;
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull LidarrMonitorType monitorNewItems() {
        return monitorNewItems;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable File folder() {
        return folder;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public @Nullable String cleanName() {
        return cleanName;
    }

    public @Nullable String sortName() {
        return sortName;
    }

    public @NotNull Set<@NotNull Tag> tags() {
        return tags;
    }

    public @NotNull Instant added() {
        return added;
    }

    public @Nullable AddOptions addOptions() {
        return addOptions;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable Statistics statistics() {
        return statistics;
    }

    @Override
    public @NotNull Instant lastUpdated() {
        Instant r = Instant.EPOCH;

        AllAlbums all = api.fetch(AllAlbums.class, this.id, false);
        for (Album a : all.items()) {
            api.update(a);
            Instant t = a.lastSearchTime();
            if (t != null && t.isAfter(r)) {
                r = t;
            }
        }

        return r;
    }

    @Override
    public @NonNull Instant lastSelected() {
        return lastSelected;
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
        if (!(o instanceof Artist artist)) return false;
        return id == artist.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", status=" + status +
                ", ended=" + ended +
                ", artistName='" + artistName + '\'' +
                ", foreignArtistId='" + foreignArtistId + '\'' +
                ", mbId='" + mbId + '\'' +
                ", tadbId=" + tadbId +
                ", discogsId=" + discogsId +
                ", allMusicId='" + allMusicId + '\'' +
                ", overview='" + overview + '\'' +
                ", artistType='" + artistType + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", links=" + links +
                ", nextAlbum=" + nextAlbum +
                ", lastAlbum=" + lastAlbum +
                ", images=" + images +
                ", members=" + members +
                ", remotePoster='" + remotePoster + '\'' +
                ", path=" + path +
                ", qualityProfile=" + qualityProfile +
                ", metadataProfile=" + metadataProfile +
                ", monitored=" + monitored +
                ", monitorNewItems=" + monitorNewItems +
                ", rootFolderPath=" + rootFolderPath +
                ", folder=" + folder +
                ", genres=" + genres +
                ", cleanName='" + cleanName + '\'' +
                ", sortName='" + sortName + '\'' +
                ", tags=" + tags +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", ratings=" + ratings +
                ", statistics=" + statistics +
                ", lastSelected=" + lastSelected +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class Member {
        private final String name;
        private final String instrument;
        private final Set<@NotNull MediaCover> images = new HashSet<>();

        public Member(@NotNull JSONObject obj) {
            this.name = StringParser.parse(obj, "name");
            this.instrument = StringParser.parse(obj, "instrument");

            JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
            if (images != null) {
                for (int i = 0; i < images.length(); i++) {
                    this.images.add(new MediaCover(images.getJSONObject(i)));
                }
            }
        }

        public @Nullable String name() {
            return name;
        }

        public @Nullable String instrument() {
            return instrument;
        }

        public @NotNull Set<@NotNull MediaCover> images() {
            return images;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Member member)) return false;
            return Objects.equals(name, member.name) && Objects.equals(instrument, member.instrument);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, instrument);
        }

        @Override
        public String toString() {
            return "Member{" +
                    "name='" + name + '\'' +
                    ", instrument='" + instrument + '\'' +
                    ", images=" + images +
                    '}';
        }
    }

    public static class AddOptions {
        private final LidarrMonitorType monitor;
        private final Set<@NotNull String> albumsToMonitor = new HashSet<>();
        private final boolean monitored;
        private final boolean searchForMissingAlbums;

        public AddOptions(@NotNull JSONObject obj) {
            this.monitor = LidarrMonitorType.parse(LidarrMonitorType.UNKNOWN, StringParser.parse(obj, "monitor"));

            JSONArray albumsToMonitor = obj.has("albumsToMonitor") ? obj.getJSONArray("albumsToMonitor") : null;
            if (albumsToMonitor != null) {
                for (int i = 0; i < albumsToMonitor.length(); i++) {
                    this.albumsToMonitor.add(albumsToMonitor.getString(i));
                }
            }

            this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
            this.searchForMissingAlbums = BooleanParser.parse(false, StringParser.parse(obj, "searchForMissingAlbums"));
        }

        public @NotNull LidarrMonitorType monitor() {
            return monitor;
        }

        public @NotNull Set<@NotNull String> albumsToMonitor() {
            return albumsToMonitor;
        }

        public boolean monitored() {
            return monitored;
        }

        public boolean searchForMissingAlbums() {
            return searchForMissingAlbums;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AddOptions that)) return false;
            return monitored == that.monitored && searchForMissingAlbums == that.searchForMissingAlbums && monitor == that.monitor && Objects.equals(albumsToMonitor, that.albumsToMonitor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(monitor, albumsToMonitor, monitored, searchForMissingAlbums);
        }

        @Override
        public String toString() {
            return "AddOptions{" +
                    "monitor=" + monitor +
                    ", albumsToMonitor=" + albumsToMonitor +
                    ", monitored=" + monitored +
                    ", searchForMissingAlbums=" + searchForMissingAlbums +
                    '}';
        }
    }

    public static class Statistics {
        private final int albumCount;
        private final int trackFileCount;
        private final int trackCount;
        private final int totalTrackCount;
        private final long sizeOnDisk;
        private final float percentOfTracks;

        public Statistics(@NotNull JSONObject obj) {
            this.albumCount = NumberParser.parseInt(-1, StringParser.parse(obj, "albumCount"));
            this.trackFileCount = NumberParser.parseInt(-1, StringParser.parse(obj, "trackFileCount"));
            this.trackCount = NumberParser.parseInt(-1, StringParser.parse(obj, "trackCount"));
            this.totalTrackCount = NumberParser.parseInt(-1, StringParser.parse(obj, "totalTrackCount"));
            this.sizeOnDisk = NumberParser.parseLong(-1L, StringParser.parse(obj, "sizeOnDisk"));
            this.percentOfTracks = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "percentOfTracks"));
        }

        public int albumCount() {
            return albumCount;
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
            return albumCount == that.albumCount && trackFileCount == that.trackFileCount && trackCount == that.trackCount && totalTrackCount == that.totalTrackCount && sizeOnDisk == that.sizeOnDisk && Float.compare(percentOfTracks, that.percentOfTracks) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(albumCount, trackFileCount, trackCount, totalTrackCount, sizeOnDisk, percentOfTracks);
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "albumCount=" + albumCount +
                    ", trackFileCount=" + trackFileCount +
                    ", trackCount=" + trackCount +
                    ", totalTrackCount=" + totalTrackCount +
                    ", sizeOnDisk=" + sizeOnDisk +
                    ", percentOfTracks=" + percentOfTracks +
                    '}';
        }
    }
}
