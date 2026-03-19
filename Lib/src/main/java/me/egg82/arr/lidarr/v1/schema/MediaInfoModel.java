package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediaInfoModel extends AbstractAPIObject {
    private final String audioFormat;
    private final int audioBitrate;
    private final int audioChannels;
    private final int audioBits;
    private final int audioSampleRate;

    public MediaInfoModel(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.audioFormat = StringParser.get(obj, "audioFormat");
        this.audioBitrate = NumberParser.getInt(-1, obj, "audioBitrate");
        this.audioChannels = NumberParser.getInt(-1, obj, "audioChannels");
        this.audioBits = NumberParser.getInt(-1, obj, "audioBits");
        this.audioSampleRate = NumberParser.getInt(-1, obj, "audioSampleRate");
    }

    public @Nullable String audioFormat() {
        return audioFormat;
    }

    public int audioBitrate() {
        return audioBitrate;
    }

    public int audioChannels() {
        return audioChannels;
    }

    public int audioBits() {
        return audioBits;
    }

    public int audioSampleRate() {
        return audioSampleRate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediaInfoModel that)) return false;
        return audioBitrate == that.audioBitrate && audioChannels == that.audioChannels && audioBits == that.audioBits && audioSampleRate == that.audioSampleRate && Objects.equals(audioFormat, that.audioFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(audioFormat, audioBitrate, audioChannels, audioBits, audioSampleRate);
    }

    @Override
    public String toString() {
        return "MediaInfoModel{" +
                "audioFormat='" + audioFormat + '\'' +
                ", audioBitrate=" + audioBitrate +
                ", audioChannels=" + audioChannels +
                ", audioBits=" + audioBits +
                ", audioSampleRate=" + audioSampleRate +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
