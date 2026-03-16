package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ratings extends AbstractAPIObject {
    private final float value;
    private final int votes;

    public Ratings(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.value = NumberParser.getFloat(-1.0F, obj, "value");
        this.votes = NumberParser.getInt(-1, obj, "votes");
    }

    public float getValue() {
        return value;
    }

    public int getVotes() {
        return votes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ratings ratings)) return false;
        return Float.compare(value, ratings.value) == 0 && votes == ratings.votes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, votes);
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "value=" + value +
                ", votes=" + votes +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
