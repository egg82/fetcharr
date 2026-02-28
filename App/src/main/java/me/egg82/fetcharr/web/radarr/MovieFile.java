package me.egg82.fetcharr.web.radarr;

import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.DurationFormatException;
import me.egg82.fetcharr.env.ParsedDateTime;
import me.egg82.fetcharr.env.ParsedDuration;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import me.egg82.fetcharr.web.common.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MovieFile extends APIObject {
    public static final MovieFile UNKNOWN = new MovieFile();

    private final String path;
    private final long size;
    private final ParsedDateTime added;
    private final String sceneName;
    private final String releaseGroup;
    private final String edition;
    private final Set<@NotNull Language> languages;
    private final QualityProfile.Quality quality;
    private final int qualityRevisionVer;
    private final int qualityRevisionReal;
    private final boolean repack;
    private final Set<@NotNull CustomFormat> customFormats;
    private final int customFormatScore;
    private final MovieFile.MediaInfo info;
    private final boolean cutoff;
    private final int id;

    public MovieFile(@NotNull JSONObject obj, @NotNull RadarrAPI api) {
        super(obj, api);

        this.path = getString("", "path");
        this.size = getLong(-1L, "size");
        this.added = getDateTime(ParsedDateTime.UNKNOWN, "dateAdded");
        this.sceneName = getString("", "sceneName");
        this.releaseGroup = getString("", "releaseGroup");
        this.edition = getString("", "edition");
        this.languages = getLanguageSet(Set.of(), "languages");
        this.quality = getQuality(QualityProfile.Quality.UNKNOWN, "quality", "quality");
        this.qualityRevisionVer = getInt(-1, "quality", "revision", "version");
        this.qualityRevisionReal = getInt(-1, "quality", "revision", "real");
        this.repack = getBoolean(false, "quality", "revision", "isRepack");
        this.customFormats = getCustomFormatSet(Set.of(), "customFormats");
        this.customFormatScore = getInt(-1, "customFormatScore");
        this.info = getMediaInfo(MovieFile.MediaInfo.UNKNOWN, "mediaInfo");
        this.cutoff = !getBoolean(false, "qualityCutoffNotMet");
        this.id = getInt(-1, "id");
    }

    private MovieFile() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.path = "";
        this.size = -1L;
        this.added = ParsedDateTime.UNKNOWN;
        this.sceneName = "";
        this.releaseGroup = "";
        this.edition = "";
        this.languages = Set.of();
        this.quality = QualityProfile.Quality.UNKNOWN;
        this.qualityRevisionVer = -1;
        this.qualityRevisionReal = -1;
        this.repack = false;
        this.customFormats = Set.of();
        this.customFormatScore = -1;
        this.info = MovieFile.MediaInfo.UNKNOWN;
        this.cutoff = false;
        this.id = -1;
    }

    public boolean unknown() { return id < 0; }

    private @NotNull MovieFile.MediaInfo getMediaInfo(@NotNull MovieFile.MediaInfo def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse JSON path {}", String.join(".", path));
            return def;
        }

        JSONObject o;
        try {
            o = traverseObj(obj).getJSONObject(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to JSON object", String.join(".", path), ex);
            return def;
        }
        return new MovieFile.MediaInfo(o, api);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieFile movieFile)) return false;
        return id == movieFile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MovieFile{" +
                "path='" + path + '\'' +
                ", size=" + size +
                ", added=" + added +
                ", sceneName='" + sceneName + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", edition='" + edition + '\'' +
                ", languages=" + languages +
                ", quality=" + quality +
                ", qualityRevisionVer=" + qualityRevisionVer +
                ", qualityRevisionReal=" + qualityRevisionReal +
                ", repack=" + repack +
                ", customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", info=" + info +
                ", cutoff=" + cutoff +
                ", id=" + id +
                '}';
    }

    public @NotNull String path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @NotNull ParsedDateTime added() {
        return added;
    }

    public @NotNull String sceneName() {
        return sceneName;
    }

    public @NotNull String releaseGroup() {
        return releaseGroup;
    }

    public @NotNull String edition() {
        return edition;
    }

    public @NotNull Set<@NotNull Language> languages() {
        return languages;
    }

    public @NotNull QualityProfile.Quality quality() {
        return quality;
    }

    public int qualityRevisionVer() {
        return qualityRevisionVer;
    }

    public int qualityRevisionReal() {
        return qualityRevisionReal;
    }

    public boolean repack() {
        return repack;
    }

    public @NotNull Set<@NotNull CustomFormat> customFormats() {
        return customFormats;
    }

    public int customFormatScore() {
        return customFormatScore;
    }

    public @NotNull MediaInfo info() {
        return info;
    }

    public boolean cutoff() {
        return cutoff;
    }

    public int id() {
        return id;
    }

    public static class MediaInfo extends APIObject {
        public static final MovieFile.MediaInfo UNKNOWN = new MovieFile.MediaInfo();

        private final int audioBitrate;
        private final float audioChannels;
        private final String audioCodec;
        private final Set<@NotNull String> languages;
        private final int audioStreams;
        private final int videoBitDepth;
        private final int videoBitrate;
        private final String videoCodec;
        private final float fps;
        private final String dynamicRange;
        private final String dynamicRangeType;
        private final int resH;
        private final int resV;
        private final ParsedDuration runtime;
        private final String scanType;
        private final Set<@NotNull String> subtitles;

        public MediaInfo(@NotNull JSONObject obj, @NotNull ArrAPI api) {
            super(obj, api);

            this.audioBitrate = getInt(-1, "audioBitrate");
            this.audioChannels = getFloat(-1.0F, "audioChannels");
            this.audioCodec = getString("", "audioCodec");
            this.languages = new HashSet<>(Arrays.asList(getString("", "audioLanguages").split("/")));
            this.audioStreams = getInt(-1, "audioStreamCount");
            this.videoBitDepth = getInt(-1, "videoBitDepth");
            this.videoBitrate = getInt(-1, "videoBitrate");
            this.videoCodec = getString("", "videoCodec");
            this.fps = getFloat(-1.0F, "videoFps");
            this.dynamicRange = getString("", "videoDynamicRange");
            this.dynamicRangeType = getString("", "videoDynamicRangeType");

            String[] res = getString("", "resolution").split("x");
            if (res.length != 2) {
                logger.warn("resolution not in expected format. Found \"{}\"", getString("", "resolution"));
                this.resH = -1;
                this.resV = -1;
            } else {
                int rh;
                int rv;
                try {
                    rh = Integer.parseInt(res[0]);
                } catch (NumberFormatException ignored) {
                    logger.warn("Could not parse horizontal resolution \"{}\"", res[0]);
                    rh = -1;
                }
                try {
                    rv = Integer.parseInt(res[1]);
                } catch (NumberFormatException ignored) {
                    logger.warn("Could not parse vertical resolution \"{}\"", res[1]);
                    rv = -1;
                }
                this.resH = rh;
                this.resV = rv;
            }

            ParsedDuration p;
            try {
                p = new ParsedDuration(getString("", "runtime"));
            } catch (DurationFormatException ignored) {
                logger.warn("Could not transform runtime to duration: {}", getString("", "runtime"));
                p = ParsedDuration.UNKNOWN;
            }
            this.runtime = p;

            this.scanType = getString("", "scanType");
            this.subtitles = new HashSet<>(Arrays.asList(getString("", "subtitles").split("/")));
        }

        private MediaInfo() {
            super(new JSONObject(), NullAPI.INSTANCE);

            this.audioBitrate = -1;
            this.audioChannels = -1;
            this.audioCodec = "";
            this.languages = Set.of();
            this.audioStreams = -1;
            this.videoBitDepth = -1;
            this.videoBitrate = -1;
            this.videoCodec = "";
            this.fps = -1.0F;
            this.dynamicRange = "";
            this.dynamicRangeType = "";
            this.resH = -1;
            this.resV = -1;
            this.runtime = ParsedDuration.UNKNOWN;
            this.scanType = "";
            this.subtitles = Set.of();
        }

        public boolean unknown() { return api instanceof NullAPI; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MediaInfo mediaInfo)) return false;
            return audioBitrate == mediaInfo.audioBitrate && Float.compare(audioChannels, mediaInfo.audioChannels) == 0 && audioStreams == mediaInfo.audioStreams && videoBitDepth == mediaInfo.videoBitDepth && videoBitrate == mediaInfo.videoBitrate && Float.compare(fps, mediaInfo.fps) == 0 && resH == mediaInfo.resH && resV == mediaInfo.resV && Objects.equals(audioCodec, mediaInfo.audioCodec) && Objects.equals(languages, mediaInfo.languages) && Objects.equals(videoCodec, mediaInfo.videoCodec) && Objects.equals(dynamicRange, mediaInfo.dynamicRange) && Objects.equals(dynamicRangeType, mediaInfo.dynamicRangeType) && Objects.equals(runtime, mediaInfo.runtime) && Objects.equals(scanType, mediaInfo.scanType) && Objects.equals(subtitles, mediaInfo.subtitles);
        }

        @Override
        public int hashCode() {
            return Objects.hash(audioBitrate, audioChannels, audioCodec, languages, audioStreams, videoBitDepth, videoBitrate, videoCodec, fps, dynamicRange, dynamicRangeType, resH, resV, runtime, scanType, subtitles);
        }

        @Override
        public String toString() {
            return "MediaInfo{" +
                    "audioBitrate=" + audioBitrate +
                    ", audioChannels=" + audioChannels +
                    ", audioCodec='" + audioCodec + '\'' +
                    ", languages=" + languages +
                    ", audioStreams=" + audioStreams +
                    ", videoBitDepth=" + videoBitDepth +
                    ", videoBitrate=" + videoBitrate +
                    ", videoCodec='" + videoCodec + '\'' +
                    ", fps=" + fps +
                    ", dynamicRange='" + dynamicRange + '\'' +
                    ", dynamicRangeType='" + dynamicRangeType + '\'' +
                    ", resH=" + resH +
                    ", resV=" + resV +
                    ", runtime=" + runtime +
                    ", scanType='" + scanType + '\'' +
                    ", subtitles=" + subtitles +
                    '}';
        }

        public int audioBitrate() {
            return audioBitrate;
        }

        public float audioChannels() {
            return audioChannels;
        }

        public @NotNull String audioCodec() {
            return audioCodec;
        }

        public @NotNull Set<@NotNull String> languages() {
            return languages;
        }

        public int audioStreams() {
            return audioStreams;
        }

        public int videoBitDepth() {
            return videoBitDepth;
        }

        public int videoBitrate() {
            return videoBitrate;
        }

        public @NotNull String videoCodec() {
            return videoCodec;
        }

        public float fps() {
            return fps;
        }

        public @NotNull String dynamicRange() {
            return dynamicRange;
        }

        public @NotNull String dynamicRangeType() {
            return dynamicRangeType;
        }

        public int resH() {
            return resH;
        }

        public int resV() {
            return resV;
        }

        public @NotNull ParsedDuration runtime() {
            return runtime;
        }

        public @NotNull String scanType() {
            return scanType;
        }

        public @NotNull Set<@NotNull String> subtitles() {
            return subtitles;
        }
    }
}
