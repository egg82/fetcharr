package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MovieStatisticsResource extends AbstractAPIObject {
    private final int movieFileCount;
    private final long sizeOnDisk;
    private final Set<@NotNull String> releaseGroups = new HashSet<>();

    public MovieStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.movieFileCount = NumberParser.getInt(-1, obj, "movieFileCount");
        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");

        JSONArray releaseGroups = obj.has("releaseGroups") && obj.get("releaseGroups") != null ? obj.getJSONArray("releaseGroups") : null;
        if (releaseGroups != null) {
            for (int i = 0; i < releaseGroups.length(); i++) {
                this.releaseGroups.add(releaseGroups.getString(i));
            }
        }
    }

    public int movieFileCount() {
        return movieFileCount;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public @NotNull Set<@NotNull String> releaseGroups() {
        return releaseGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieStatisticsResource that)) return false;
        return movieFileCount == that.movieFileCount && sizeOnDisk == that.sizeOnDisk && Objects.equals(releaseGroups, that.releaseGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieFileCount, sizeOnDisk, releaseGroups);
    }

    @Override
    public String toString() {
        return "MovieStatisticsResource{" +
                "movieFileCount=" + movieFileCount +
                ", sizeOnDisk=" + sizeOnDisk +
                ", releaseGroups=" + releaseGroups +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
