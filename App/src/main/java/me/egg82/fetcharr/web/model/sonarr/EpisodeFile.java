package me.egg82.fetcharr.web.model.sonarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.ResolutionValue;
import me.egg82.fetcharr.unit.TimeValue;
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

public class EpisodeFile extends AbstractAPIObject<EpisodeFile> {
    public static final EpisodeFile UNKNOWN = new EpisodeFile(ArrAPI.UNKNOWN, -1);

    private final int id;

    private final Set<@NotNull CustomFormat> customFormats = new HashSet<>();
    private int customFormatScore;
    private int indexerFlags;
    private Instant dateAdded;
    private final Set<@NotNull Language> languages = new HashSet<>();
    private MediaInfoResource mediaInfo;
    private File path;
    private QualityModel quality;
    private boolean qualityCutoffNotMet;
    private File relativePath;
    private String releaseGroup;
    private SonarrReleaseType releaseType;
    private String sceneName;
    private int seasonNumber;
    private Series series;
    private long size;

    public EpisodeFile(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/episodefile/" + id);
        this.id = id;
    }

    @Override
    public EpisodeFile fetch(@NotNull String apiKey) {
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
                if (this.series != null) {
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

        this.customFormats.clear();
        JSONArray customFormats = obj.has("customFormats") ? obj.getJSONArray("customFormats") : null;
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                int id = NumberParser.parseInt(-1, StringParser.parse(customFormats.getJSONObject(i), "id"));
                if (id >= 0) {
                    this.customFormats.add(api.fetch(CustomFormat.class, id));
                }
            }
        }

        this.customFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "customFormatScore"));
        this.dateAdded = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "dateAdded"));
        this.indexerFlags = NumberParser.parseInt(-1, StringParser.parse(obj, "indexerFlags"));

        this.languages.clear();
        JSONArray languages = obj.has("languages") ? obj.getJSONArray("languages") : null;
        if (languages != null) {
            for (int i = 0; i < languages.length(); i++) {
                this.languages.add(api.fetch(Language.class, id));
            }
        }

        this.mediaInfo = obj.has("mediaInfo") ? new MediaInfoResource(obj.getJSONObject("mediaInfo")) : null;
        this.path = FileParser.parse(StringParser.parse(obj, "path"));
        this.quality = obj.has("quality") ? new QualityModel(obj.getJSONObject("quality")) : null;
        this.qualityCutoffNotMet = BooleanParser.parse(false, StringParser.parse(obj, "qualityCutoffNotMet"));
        this.relativePath = FileParser.parse(StringParser.parse(obj, "relativePath"));
        this.releaseGroup = StringParser.parse(obj, "releaseGroup");
        this.releaseType = SonarrReleaseType.parse(SonarrReleaseType.UNKNOWN, StringParser.parse(obj, "releaseType"));
        this.sceneName = StringParser.parse(obj, "sceneName");
        this.seasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "seasonNumber"));
        this.size = NumberParser.parseLong(-1L, StringParser.parse(obj, "size"));

        this.series = api.fetch(Series.class, NumberParser.parseInt(-1, StringParser.parse(obj, "seriesId")));
    }

    public int id() {
        return id;
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

    public @NotNull Instant dateAdded() {
        return dateAdded;
    }

    public @NotNull Set<@NotNull Language> languages() {
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

    public @Nullable File relativePath() {
        return relativePath;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @NotNull SonarrReleaseType releaseType() {
        return releaseType;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public int seasonNumber() {
        return seasonNumber;
    }

    public @NotNull Series series() {
        return series;
    }

    public long size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EpisodeFile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EpisodeFile{" +
                "id=" + id +
                ", customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", indexerFlags=" + indexerFlags +
                ", dateAdded=" + dateAdded +
                ", languages=" + languages +
                ", mediaInfo=" + mediaInfo +
                ", path='" + path + '\'' +
                ", quality=" + quality +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", relativePath='" + relativePath + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", releaseType=" + releaseType +
                ", sceneName='" + sceneName + '\'' +
                ", seasonNumber=" + seasonNumber +
                ", series=" + series +
                ", size=" + size +
                '}';
    }

    public static class MediaInfoResource {
        private final int id;

        private final long audioBitrate;
        private final float audioChannels;
        private final String audioCodec;
        private final Set<@NotNull String> audioLanguages = new HashSet<>();
        private final int audioStreamCount;
        private final ResolutionValue resolution;
        private final Duration runTime;
        private final ScanType scanType;
        private final Set<@NotNull String> subtitles = new HashSet<>();
        private final int videoBitDepth;
        private final long videoBitrate;
        private final String videoCodec;
        private final String videoDynamicRange;
        private final String videoDynamicRangeType;
        private final float videoFps;

        public MediaInfoResource(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.audioBitrate = NumberParser.parseLong(-1L, StringParser.parse(obj, "audioBitrate"));
            this.audioChannels = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "audioChannels"));
            this.audioCodec = StringParser.parse(obj, "audioCodec");

            String audioLanguages = StringParser.parse(obj, "audioLanguages");
            if (audioLanguages != null) {
                this.audioLanguages.addAll(Arrays.asList(audioLanguages.trim().split(",")));
            }

            this.audioStreamCount = NumberParser.parseInt(-1, StringParser.parse(obj, "audioStreamCount"));
            this.resolution = ResolutionParser.parse(StringParser.parse(obj, "resolution"));
            this.runTime = DurationParser.parse(StringParser.parse(obj, "runTime"));
            this.scanType = ScanType.parse(StringParser.parse(obj, "scanType"));

            String subtitles = StringParser.parse(obj, "subtitles");
            if (subtitles != null) {
                this.subtitles.addAll(Arrays.asList(subtitles.trim().split(",")));
            }

            this.videoBitDepth = NumberParser.parseInt(-1, StringParser.parse(obj, "videoBitDepth"));
            this.videoBitrate = NumberParser.parseLong(-1L, StringParser.parse(obj, "videoBitrate"));
            this.videoCodec = StringParser.parse(obj, "videoCodec");
            this.videoDynamicRange = StringParser.parse(obj, "videoDynamicRange");
            this.videoDynamicRangeType = StringParser.parse(obj, "videoDynamicRangeType");
            this.videoFps = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "videoFps"));
        }

        public int id() {
            return id;
        }

        public long audioBitrate() {
            return audioBitrate;
        }

        public float audioChannels() {
            return audioChannels;
        }

        public @Nullable String audioCodec() {
            return audioCodec;
        }

        public @NotNull Set<@NotNull String> audioLanguages() {
            return audioLanguages;
        }

        public int audioStreamCount() {
            return audioStreamCount;
        }

        public @Nullable ResolutionValue resolution() {
            return resolution;
        }

        public @Nullable Duration runTime() {
            return runTime;
        }

        public @Nullable ScanType scanType() {
            return scanType;
        }

        public @NotNull Set<@NotNull String> subtitles() {
            return subtitles;
        }

        public int videoBitDepth() {
            return videoBitDepth;
        }

        public long videoBitrate() {
            return videoBitrate;
        }

        public @Nullable String videoCodec() {
            return videoCodec;
        }

        public @Nullable String videoDynamicRange() {
            return videoDynamicRange;
        }

        public @Nullable String videoDynamicRangeType() {
            return videoDynamicRangeType;
        }

        public float videoFps() {
            return videoFps;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MediaInfoResource that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "MediaInfoResource{" +
                    "id=" + id +
                    ", audioBitrate=" + audioBitrate +
                    ", audioChannels=" + audioChannels +
                    ", audioCodec='" + audioCodec + '\'' +
                    ", audioLanguages=" + audioLanguages +
                    ", audioStreamCount=" + audioStreamCount +
                    ", resolution=" + resolution +
                    ", runTime=" + runTime +
                    ", scanType=" + scanType +
                    ", subtitles=" + subtitles +
                    ", videoBitDepth=" + videoBitDepth +
                    ", videoBitrate=" + videoBitrate +
                    ", videoCodec='" + videoCodec + '\'' +
                    ", videoDynamicRange='" + videoDynamicRange + '\'' +
                    ", videoDynamicRangeType='" + videoDynamicRangeType + '\'' +
                    ", videoFps=" + videoFps +
                    '}';
        }
    }

    public static class QualityModel {
        private final Quality quality;
        private final Revision revision;

        public QualityModel(@NotNull JSONObject obj) {
            this.quality = obj.has("quality") ? new Quality(obj.getJSONObject("quality")) : null;
            this.revision = obj.has("revision") ? new Revision(obj.getJSONObject("revision")) : null;
        }

        public @Nullable Quality quality() {
            return quality;
        }

        public @Nullable Revision revision() {
            return revision;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof QualityModel that)) return false;
            return Objects.equals(quality, that.quality) && Objects.equals(revision, that.revision);
        }

        @Override
        public int hashCode() {
            return Objects.hash(quality, revision);
        }

        @Override
        public String toString() {
            return "QualityModel{" +
                    "quality=" + quality +
                    ", revision=" + revision +
                    '}';
        }

        public static class Revision {
            private final boolean isRepack;
            private final int real;
            private final int version;

            public Revision(@NotNull JSONObject obj) {
                this.isRepack = BooleanParser.parse(false, StringParser.parse(obj, "isRepack"));
                this.real = NumberParser.parseInt(-1, StringParser.parse(obj, "real"));
                this.version = NumberParser.parseInt(-1, StringParser.parse(obj, "version"));
            }

            public boolean isRepack() {
                return isRepack;
            }

            public int real() {
                return real;
            }

            public int version() {
                return version;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Revision revision)) return false;
                return real == revision.real && version == revision.version;
            }

            @Override
            public int hashCode() {
                return Objects.hash(real, version);
            }

            @Override
            public String toString() {
                return "Revision{" +
                        "isRepack=" + isRepack +
                        ", real=" + real +
                        ", version=" + version +
                        '}';
            }
        }
    }
}
