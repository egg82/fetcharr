package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SeriesType {
    STANDARD("standard"),
    DAILY("daily"),
    ANIME("anime");

    private final String apiName;
    SeriesType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SeriesType get(@NotNull SeriesType def, @Nullable JSONObject obj, @Nullable String key) {
        SeriesType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable SeriesType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull SeriesType parse(@NotNull SeriesType def, @Nullable String val) {
        SeriesType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SeriesType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SeriesType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
