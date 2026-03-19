package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ratings extends AbstractAPIObject {
    private final int votes;
    private final float value;

    public Ratings(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.votes = NumberParser.getInt(-1, obj, "votes");
        this.value = NumberParser.getFloat(-1.0F, obj, "value");
    }

    public int votes() {
        return votes;
    }

    public float value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ratings ratings)) return false;
        return votes == ratings.votes && Float.compare(value, ratings.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(votes, value);
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "votes=" + votes +
                ", value=" + value +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
