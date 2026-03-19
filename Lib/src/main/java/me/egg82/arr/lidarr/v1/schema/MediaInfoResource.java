package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediaInfoResource extends AbstractAPIObject {
    private final int id;
    private final float audioChannels;
    private final String audioBitRate;
    private final String audioCodec;
    private final String audioBits;
    private final String audioSampleRate;

    public MediaInfoResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.audioChannels = NumberParser.getFloat(-1.0F, obj, "audioChannels");
        this.audioBitRate = StringParser.get(obj, "audioBitRate");
        this.audioCodec = StringParser.get(obj, "audioCodec");
        this.audioBits = StringParser.get(obj, "audioBits");
        this.audioSampleRate = StringParser.get(obj, "audioSampleRate");
    }

    public int id() {
        return id;
    }

    public float audioChannels() {
        return audioChannels;
    }

    public @Nullable String audioBitRate() {
        return audioBitRate;
    }

    public @Nullable String audioCodec() {
        return audioCodec;
    }

    public @Nullable String audioBits() {
        return audioBits;
    }

    public @Nullable String audioSampleRate() {
        return audioSampleRate;
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
                ", audioChannels=" + audioChannels +
                ", audioBitRate='" + audioBitRate + '\'' +
                ", audioCodec='" + audioCodec + '\'' +
                ", audioBits='" + audioBits + '\'' +
                ", audioSampleRate='" + audioSampleRate + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
