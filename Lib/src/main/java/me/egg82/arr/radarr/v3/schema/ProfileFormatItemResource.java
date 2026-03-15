package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProfileFormatItemResource extends AbstractAPIObject {
    private final int id;
    private final int format;
    private final String name;
    private final int score;

    public ProfileFormatItemResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.format = NumberParser.getInt(-1, obj, "format");
        this.name = StringParser.get(obj, "name");
        this.score = NumberParser.getInt(-1, obj, "score");
    }

    public int id() {
        return id;
    }

    public int format() {
        return format;
    }

    public @Nullable String name() {
        return name;
    }

    public int score() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileFormatItemResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProfileFormatItemResource{" +
                "id=" + id +
                ", format=" + format +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
