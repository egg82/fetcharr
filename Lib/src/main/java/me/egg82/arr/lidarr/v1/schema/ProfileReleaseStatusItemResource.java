package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProfileReleaseStatusItemResource extends AbstractAPIObject {
    private final int id;
    private final ReleaseStatus releaseStatus;
    private final boolean allowed;

    public ProfileReleaseStatusItemResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.releaseStatus = ObjectParser.get(ReleaseStatus.class, api, obj, "releaseStatus");
        this.allowed = BooleanParser.get(false, obj, "allowed");
    }

    public int id() {
        return id;
    }

    public @Nullable ReleaseStatus releaseStatus() {
        return releaseStatus;
    }

    public boolean allowed() {
        return allowed;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileReleaseStatusItemResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProfileReleaseStatusItemResource{" +
                "id=" + id +
                ", releaseStatus=" + releaseStatus +
                ", allowed=" + allowed +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
