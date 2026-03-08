package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.DurationParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.ResolutionParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.unit.ResolutionValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MediaInfo {
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

    public MediaInfo(@NotNull JSONObject obj) {
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
        if (!(o instanceof MediaInfo that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
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
