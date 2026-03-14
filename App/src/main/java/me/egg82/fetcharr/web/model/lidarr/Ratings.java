package me.egg82.fetcharr.web.model.lidarr;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ratings {
    private final int votes;
    private final float value;

    public Ratings(@NotNull JSONObject obj) {
        this.votes = NumberParser.parseInt(-1, StringParser.parse(obj, "votes"));
        this.value = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "value"));
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
                '}';
    }
}
