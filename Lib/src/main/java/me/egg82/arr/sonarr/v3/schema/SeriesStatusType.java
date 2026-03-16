package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SeriesStatusType {
    CONTINUING("continuing"),
    ENDED("ended"),
    UPCOMING("upcoming"),
    DELETED("deleted");

    private final String apiName;
    SeriesStatusType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SeriesStatusType get(@NotNull SeriesStatusType def, @Nullable JSONObject obj, @Nullable String key) {
        SeriesStatusType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable SeriesStatusType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull SeriesStatusType parse(@NotNull SeriesStatusType def, @Nullable String val) {
        SeriesStatusType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SeriesStatusType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SeriesStatusType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
