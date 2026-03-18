package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Quality extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final QualitySource source;
    private final int resolution;

    public Quality(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.source = QualitySource.get(QualitySource.UNKNOWN, obj, "source");
        this.resolution = NumberParser.getInt(-1, obj, "resolution");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull QualitySource source() {
        return source;
    }

    public int resolution() {
        return resolution;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quality quality)) return false;
        return id == quality.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Quality{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", source=" + source +
                ", resolution=" + resolution +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
