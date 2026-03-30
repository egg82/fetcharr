package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditionListLazyLoaded extends AbstractAPIObject {
    private final PVector<@NotNull Edition> value;
    private final boolean isLoaded;

    public EditionListLazyLoaded(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api,obj);

        JSONArray value = obj.has("value") && obj.get("value") != null ? obj.getJSONArray("value") : null;
        List<@NotNull Edition> valueL = new ArrayList<>();
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                valueL.add(new Edition(api, value.getJSONObject(i)));
            }
        }
        this.value = TreePVector.from(valueL);

        this.isLoaded = BooleanParser.get(false, obj, "isLoaded");
    }

    public @NotNull PVector<@NotNull Edition> value() {
        return value;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EditionListLazyLoaded that)) return false;
        return isLoaded == that.isLoaded && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isLoaded);
    }

    @Override
    public String toString() {
        return "EditionListLazyLoaded{" +
                "value=" + value +
                ", isLoaded=" + isLoaded +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
