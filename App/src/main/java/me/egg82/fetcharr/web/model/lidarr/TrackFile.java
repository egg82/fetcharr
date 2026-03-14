package me.egg82.fetcharr.web.model.lidarr;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.TimeValue;
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

public class TrackFile extends AbstractAPIObject<TrackFile> {
    public static final TrackFile UNKNOWN = new TrackFile(ArrAPI.UNKNOWN, -1);

    private final int id;

    private Artist artist;
    private Album album;
    private File path;
    private long size;
    private Instant dateAdded;
    private String sceneName;
    private String releaseGroup;
    private QualityModel quality;
    private int qualityWeight;
    private final Set<@NotNull CustomFormat> customFormats = new HashSet<>();
    private int customFormatScore;
    private int indexerFlags;
    private MediaInfo mediaInfo;
    private boolean qualityCutoffNotMet;
    private TrackInfo audioTags;

    public TrackFile(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/trackfile/" + id);
        this.id = id;
    }

    @Override
    public TrackFile fetch(@NotNull String apiKey) {
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
                if (this.artist != null) {
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

        this.album = api.fetch(Album.class, NumberParser.parseInt(-1, StringParser.parse(obj, "albumId")), true);
        this.path = FileParser.parse(StringParser.parse(obj, "path"));
        this.size = NumberParser.parseLong(-1L, StringParser.parse(obj, "size"));
        this.dateAdded = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "dateAdded"));
        this.sceneName = StringParser.parse(obj, "sceneName");
        this.releaseGroup = StringParser.parse(obj, "releaseGroup");
        this.quality = obj.has("quality") ? new QualityModel(obj.getJSONObject("quality")) : null;
        this.qualityWeight = NumberParser.parseInt(-1, StringParser.parse(obj, "qualityWeight"));

        this.customFormats.clear();
        JSONArray customFormats = obj.has("customFormats") ? obj.getJSONArray("customFormats") : null;
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                int id = NumberParser.parseInt(-1, StringParser.parse(customFormats.getJSONObject(i), "id"));
                if (id >= 0) {
                    this.customFormats.add(api.fetch(CustomFormat.class, id, false));
                }
            }
        }

        this.customFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "customFormatScore"));
        this.indexerFlags = NumberParser.parseInt(-1, StringParser.parse(obj, "indexerFlags"));
        this.mediaInfo = obj.has("mediaInfo") ? new MediaInfo(obj.getJSONObject("mediaInfo")) : null;
        this.qualityCutoffNotMet = BooleanParser.parse(false, StringParser.parse(obj, "qualityCutoffNotMet"));
        this.audioTags = obj.has("audioTags") ? new TrackInfo(obj.getJSONObject("audioTags")) : null;

        this.artist = api.fetch(Artist.class, NumberParser.parseInt(-1, StringParser.parse(obj, "artistId")), true);
    }

    public int id() {
        return id;
    }

    public @Nullable Artist artist() {
        return artist;
    }

    public @Nullable Album album() {
        return album;
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

    public @Nullable QualityModel quality() {
        return quality;
    }

    public int qualityWeight() {
        return qualityWeight;
    }

    public @NotNull Set<@NotNull CustomFormat> customFormats() {
        return customFormats;
    }

    public int customFormatScore() {
        return customFormatScore;
    }

    public int indexerFlags() {
        return indexerFlags;
    }

    public @Nullable MediaInfo mediaInfo() {
        return mediaInfo;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    public @Nullable TrackInfo audioTags() {
        return audioTags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TrackFile trackFile)) return false;
        return id == trackFile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrackFile{" +
                "id=" + id +
                ", artist=" + artist +
                ", album=" + album +
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
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class TrackInfo {
        private final String title;
        private final String cleanTitle;
        private final String artistTitle;
        private final String albumTitle;
        private final ArtistTitle artistTitleInfo;
        private final String artistMBId;
        private final String albumMBId;
        private final String releaseMBId;
        private final String recordingMBId;
        private final String trackMBId;
        private final int discNumber;
        private final int discCount;
        private final Country country;
        private final int year;
        private final String label;
        private final String catalogNumber;
        private final String disambiguation;
        private final Duration duration;
        private final QualityModel quality;
        private final MediaInfo mediaInfo;
        private final IntSet trackNumbers = new IntArraySet();
        private final String releaseGroup;
        private final String releaseHash;

        public TrackInfo(@NotNull JSONObject obj) {
            this.title = StringParser.parse(obj, "title");
            this.cleanTitle = StringParser.parse(obj, "cleanTitle");
            this.artistTitle = StringParser.parse(obj, "artistTitle");
            this.albumTitle = StringParser.parse(obj, "albumTitle");
            this.artistTitleInfo = obj.has("artistTitleInfo") ? new ArtistTitle(obj.getJSONObject("artistTitleInfo")) : null;
            this.artistMBId = StringParser.parse(obj, "artistMBId");
            this.albumMBId = StringParser.parse(obj, "albumMBId");
            this.releaseMBId = StringParser.parse(obj, "releaseMBId");
            this.recordingMBId = StringParser.parse(obj, "recordingMBId");
            this.trackMBId = StringParser.parse(obj, "trackMBId");
            this.discNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "discNumber"));
            this.discCount = NumberParser.parseInt(-1, StringParser.parse(obj, "discCount"));
            this.country = obj.has("country") ? new Country(obj.getJSONObject("country")) : null;
            this.year = NumberParser.parseInt(-1, StringParser.parse(obj, "year"));
            this.label = StringParser.parse(obj, "label");
            this.catalogNumber = StringParser.parse(obj, "catalogNumber");
            this.disambiguation = StringParser.parse(obj, "disambiguation");
            this.duration = DurationParser.parse(StringParser.parse(obj, "duration"));
            this.quality = obj.has("quality") ? new QualityModel(obj.getJSONObject("quality")) : null;
            this.mediaInfo = obj.has("mediaInfo") ? new MediaInfo(obj.getJSONObject("mediaInfo")) : null;

            JSONArray trackNumbers = obj.has("trackNumbers") ? obj.getJSONArray("trackNumbers") : null;
            if (trackNumbers != null) {
                for (int i = 0; i < trackNumbers.length(); i++) {
                    this.trackNumbers.add(NumberParser.parseInt(-1, trackNumbers.getString(i)));
                }
            }

            this.releaseGroup = StringParser.parse(obj, "releaseGroup");
            this.releaseHash = StringParser.parse(obj, "releaseHash");
        }

        public @Nullable String title() {
            return title;
        }

        public @Nullable String cleanTitle() {
            return cleanTitle;
        }

        public @Nullable String artistTitle() {
            return artistTitle;
        }

        public @Nullable String albumTitle() {
            return albumTitle;
        }

        public @Nullable ArtistTitle artistTitleInfo() {
            return artistTitleInfo;
        }

        public @Nullable String artistMBId() {
            return artistMBId;
        }

        public @Nullable String albumMBId() {
            return albumMBId;
        }

        public @Nullable String releaseMBId() {
            return releaseMBId;
        }

        public @Nullable String recordingMBId() {
            return recordingMBId;
        }

        public @Nullable String trackMBId() {
            return trackMBId;
        }

        public int discNumber() {
            return discNumber;
        }

        public int discCount() {
            return discCount;
        }

        public @Nullable Country country() {
            return country;
        }

        public int year() {
            return year;
        }

        public @Nullable String label() {
            return label;
        }

        public @Nullable String catalogNumber() {
            return catalogNumber;
        }

        public @Nullable String disambiguation() {
            return disambiguation;
        }

        public @Nullable Duration duration() {
            return duration;
        }

        public @Nullable QualityModel quality() {
            return quality;
        }

        public @Nullable MediaInfo mediaInfo() {
            return mediaInfo;
        }

        public @NotNull IntSet trackNumbers() {
            return trackNumbers;
        }

        public @Nullable String releaseGroup() {
            return releaseGroup;
        }

        public @Nullable String releaseHash() {
            return releaseHash;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TrackInfo trackInfo)) return false;
            return Objects.equals(title, trackInfo.title) && Objects.equals(artistTitle, trackInfo.artistTitle) && Objects.equals(albumTitle, trackInfo.albumTitle) && Objects.equals(artistMBId, trackInfo.artistMBId) && Objects.equals(albumMBId, trackInfo.albumMBId) && Objects.equals(releaseMBId, trackInfo.releaseMBId) && Objects.equals(recordingMBId, trackInfo.recordingMBId) && Objects.equals(trackMBId, trackInfo.trackMBId) && Objects.equals(disambiguation, trackInfo.disambiguation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, artistTitle, albumTitle, artistMBId, albumMBId, releaseMBId, recordingMBId, trackMBId, disambiguation);
        }

        @Override
        public String toString() {
            return "TrackInfo{" +
                    "title='" + title + '\'' +
                    ", cleanTitle='" + cleanTitle + '\'' +
                    ", artistTitle='" + artistTitle + '\'' +
                    ", albumTitle='" + albumTitle + '\'' +
                    ", artistTitleInfo=" + artistTitleInfo +
                    ", artistMBId='" + artistMBId + '\'' +
                    ", albumMBId='" + albumMBId + '\'' +
                    ", releaseMBId='" + releaseMBId + '\'' +
                    ", recordingMBId='" + recordingMBId + '\'' +
                    ", trackMBId='" + trackMBId + '\'' +
                    ", discNumber=" + discNumber +
                    ", discCount=" + discCount +
                    ", country=" + country +
                    ", year=" + year +
                    ", label='" + label + '\'' +
                    ", catalogNumber='" + catalogNumber + '\'' +
                    ", disambiguation='" + disambiguation + '\'' +
                    ", duration=" + duration +
                    ", quality=" + quality +
                    ", mediaInfo=" + mediaInfo +
                    ", trackNumbers=" + trackNumbers +
                    ", releaseGroup='" + releaseGroup + '\'' +
                    ", releaseHash='" + releaseHash + '\'' +
                    '}';
        }

        public static class ArtistTitle {
            private final String title;
            private final String titleWithoutYear;
            private final int year;

            public ArtistTitle(@NotNull JSONObject obj) {
                this.title = StringParser.parse(obj, "title");
                this.titleWithoutYear = StringParser.parse(obj, "titleWithoutYear");
                this.year = NumberParser.parseInt(-1, StringParser.parse(obj, "year"));
            }

            public @Nullable String title() {
                return title;
            }

            public @Nullable String titleWithoutYear() {
                return titleWithoutYear;
            }

            public int year() {
                return year;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof ArtistTitle that)) return false;
                return year == that.year && Objects.equals(title, that.title) && Objects.equals(titleWithoutYear, that.titleWithoutYear);
            }

            @Override
            public int hashCode() {
                return Objects.hash(title, titleWithoutYear, year);
            }

            @Override
            public String toString() {
                return "ArtistTitle{" +
                        "title='" + title + '\'' +
                        ", titleWithoutYear='" + titleWithoutYear + '\'' +
                        ", year=" + year +
                        '}';
            }
        }

        public static class Country {
            private final String twoLetterCode;
            private final String name;

            public Country(@NotNull JSONObject obj) {
                this.twoLetterCode = StringParser.parse(obj, "twoLetterCode");
                this.name = StringParser.parse(obj, "name");
            }

            public @Nullable String twoLetterCode() {
                return twoLetterCode;
            }

            public @Nullable String name() {
                return name;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Country country)) return false;
                return Objects.equals(twoLetterCode, country.twoLetterCode) && Objects.equals(name, country.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(twoLetterCode, name);
            }

            @Override
            public String toString() {
                return "Country{" +
                        "twoLetterCode='" + twoLetterCode + '\'' +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
