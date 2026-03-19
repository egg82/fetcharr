package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfilePrimaryAlbumTypeItemResource extends AbstractAPIObject {
    private final int id;
    private final PrimaryAlbumType albumType;
    private final boolean allowed;

    public ProfilePrimaryAlbumTypeItemResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.albumType = ObjectParser.get(PrimaryAlbumType.class, api, obj, "albumType");
        this.allowed = BooleanParser.get(false, obj, "allowed");
    }

    public int id() {
        return id;
    }

    public @Nullable PrimaryAlbumType albumType() {
        return albumType;
    }

    public boolean allowed() {
        return allowed;
    }

    @Override
    public String toString() {
        return "ProfilePrimaryAlbumTypeItemResource{" +
                "id=" + id +
                ", albumType=" + albumType +
                ", allowed=" + allowed +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
