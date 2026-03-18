package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TagResource extends AbstractAPIObject {
    private final int id;
    private final String label;

    public TagResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.label = StringParser.get(obj, "label");
    }

    public int id() {
        return id;
    }

    public @Nullable String label() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TagResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TagResource{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
