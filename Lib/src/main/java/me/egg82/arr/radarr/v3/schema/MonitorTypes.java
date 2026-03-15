package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MonitorTypes {
    MOVIE_ONLY("movieOnly"),
    MOVIE_AND_COLLECTION("movieAndCollection"),
    NONE("none");

    private final String apiName;
    MonitorTypes(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull MonitorTypes get(@NotNull MonitorTypes def, @Nullable JSONObject obj, @Nullable String key) {
        MonitorTypes r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable MonitorTypes get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull MonitorTypes parse(@NotNull MonitorTypes def, @Nullable String val) {
        MonitorTypes r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable MonitorTypes parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (MonitorTypes e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
