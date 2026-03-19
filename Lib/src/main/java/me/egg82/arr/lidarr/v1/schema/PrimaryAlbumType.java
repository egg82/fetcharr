package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PrimaryAlbumType extends AbstractAPIObject {
    private final int id;
    private final String name;

    public PrimaryAlbumType(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PrimaryAlbumType that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PrimaryAlbumType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
