package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class QualityProfileLazyLoaded extends AbstractAPIObject {
    private final QualityProfile value;
    private final boolean isLoaded;

    public QualityProfileLazyLoaded(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api,obj);

        this.value = ObjectParser.get(QualityProfile.class, api, obj, "value");
        this.isLoaded = BooleanParser.get(false, obj, "isLoaded");
    }

    public @Nullable QualityProfile value() {
        return value;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfileLazyLoaded that)) return false;
        return isLoaded == that.isLoaded && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isLoaded);
    }

    @Override
    public String toString() {
        return "QualityProfileLazyLoaded{" +
                "value=" + value +
                ", isLoaded=" + isLoaded +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
