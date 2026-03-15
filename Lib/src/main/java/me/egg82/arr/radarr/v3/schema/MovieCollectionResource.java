package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MovieCollectionResource extends AbstractAPIObject {
    private final String title;
    private final int tmdbId;

    public MovieCollectionResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.title = StringParser.get(obj, "title");
        this.tmdbId = NumberParser.getInt(-1, obj, "tmdbId");
    }

    public @Nullable String title() {
        return title;
    }

    public int tmdbId() {
        return tmdbId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieCollectionResource that)) return false;
        return tmdbId == that.tmdbId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tmdbId);
    }

    @Override
    public String toString() {
        return "MovieCollectionResource{" +
                "title='" + title + '\'' +
                ", tmdbId=" + tmdbId +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
