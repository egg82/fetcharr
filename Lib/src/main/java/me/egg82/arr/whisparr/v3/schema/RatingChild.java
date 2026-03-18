package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RatingChild extends AbstractAPIObject {
    private final int votes;
    private final float value;
    private final RatingType type;

    public RatingChild(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.votes = NumberParser.getInt(-1, obj, "votes");
        this.value = NumberParser.getFloat(-1.0F, obj, "value");
        this.type = RatingType.get(RatingType.USER, obj, "type");
    }

    public int votes() {
        return votes;
    }

    public float value() {
        return value;
    }

    public @NotNull RatingType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RatingChild that)) return false;
        return votes == that.votes && Float.compare(value, that.value) == 0 && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(votes, value, type);
    }

    @Override
    public String toString() {
        return "RatingChild{" +
                "votes=" + votes +
                ", value=" + value +
                ", type=" + type +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
