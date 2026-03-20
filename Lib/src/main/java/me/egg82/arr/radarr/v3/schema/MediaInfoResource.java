package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.DurationParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ResolutionParser;
import me.egg82.arr.parse.StringParser;
import me.egg82.arr.unit.ResolutionValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.TreePSet;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

public class MediaInfoResource extends AbstractAPIObject {
    private final int id;
    private final long audioBitrate;
    private final float audioChannels;
    private final String audioCodec;
    private final PSet<@NotNull String> audioLanguages;
    private final int audioStreamCount;
    private final int videoBitDepth;
    private final long videoBitrate;
    private final String videoCodec;
    private final float videoFps;
    private final String videoDynamicRange;
    private final String videoDynamicRangeType;
    private final ResolutionValue resolution;
    private final Duration runTime;
    private final String scanType;
    private final PSet<@NotNull String> subtitles;

    public MediaInfoResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.audioBitrate = NumberParser.getLong(-1L, obj, "audioBitrate");
        this.audioChannels = NumberParser.getFloat(-1.0F, obj, "audioChannels");
        this.audioCodec = StringParser.get(obj, "audioCodec");

        String audioLanguages = StringParser.get(obj, "audioLanguages");
        this.audioLanguages = audioLanguages != null ? TreePSet.from(Arrays.asList(audioLanguages.trim().split(","))) : TreePSet.empty();

        this.audioStreamCount = NumberParser.getInt(-1, obj, "audioStreamCount");
        this.videoBitDepth = NumberParser.getInt(-1, obj, "videoBitDepth");
        this.videoBitrate = NumberParser.getLong(-1L, obj, "videoBitrate");
        this.videoCodec = StringParser.get(obj, "videoCodec");
        this.videoFps = NumberParser.getFloat(-1.0F, obj, "videoFps");
        this.videoDynamicRange = StringParser.get(obj, "videoDynamicRange");
        this.videoDynamicRangeType = StringParser.get(obj, "videoDynamicRangeType");
        this.resolution = ResolutionParser.get(obj, "resolution");
        this.runTime = DurationParser.get(obj, "runTime");
        this.scanType = StringParser.get(obj, "scanType");

        String subtitles = StringParser.get(obj, "subtitles");
        this.subtitles = subtitles != null ? TreePSet.from(Arrays.asList(subtitles.trim().split(","))) : TreePSet.empty();
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

    public @NotNull PSet<@NotNull String> audioLanguages() {
        return audioLanguages;
    }

    public int audioStreamCount() {
        return audioStreamCount;
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

    public float videoFps() {
        return videoFps;
    }

    public @Nullable String videoDynamicRange() {
        return videoDynamicRange;
    }

    public @Nullable String videoDynamicRangeType() {
        return videoDynamicRangeType;
    }

    public @Nullable ResolutionValue resolution() {
        return resolution;
    }

    public @Nullable Duration runTime() {
        return runTime;
    }

    public @Nullable String scanType() {
        return scanType;
    }

    public @NotNull PSet<@NotNull String> subtitles() {
        return subtitles;
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
                ", videoBitDepth=" + videoBitDepth +
                ", videoBitrate=" + videoBitrate +
                ", videoCodec='" + videoCodec + '\'' +
                ", videoFps=" + videoFps +
                ", videoDynamicRange='" + videoDynamicRange + '\'' +
                ", videoDynamicRangeType='" + videoDynamicRangeType + '\'' +
                ", resolution='" + resolution + '\'' +
                ", runTime=" + runTime +
                ", scanType='" + scanType + '\'' +
                ", subtitles=" + subtitles +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
