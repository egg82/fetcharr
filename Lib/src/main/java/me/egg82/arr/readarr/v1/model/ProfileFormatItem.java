package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProfileFormatItem extends AbstractAPIObject {
    private final CustomFormat format;
    private final int score;

    public ProfileFormatItem(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.format = ObjectParser.get(CustomFormat.class, api, obj, "format");
        this.score = NumberParser.getInt(-1, obj, "score");
    }

    public @Nullable CustomFormat format() {
        return format;
    }

    public int score() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileFormatItem that)) return false;
        return score == that.score && Objects.equals(format, that.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, score);
    }

    @Override
    public String toString() {
        return "ProfileFormatItem{" +
                "format=" + format +
                ", score=" + score +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
