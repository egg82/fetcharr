package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MovieStatusType {
    TBA("tba"),
    ANNOUNCED("announced"),
    IN_CINEMAS("inCinemas"),
    RELEASED("released"),
    DELETED("deleted");

    private final String apiName;
    MovieStatusType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull MovieStatusType get(@NotNull MovieStatusType def, @Nullable JSONObject obj, @Nullable String key) {
        MovieStatusType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable MovieStatusType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull MovieStatusType parse(@NotNull MovieStatusType def, @Nullable String val) {
        MovieStatusType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable MovieStatusType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (MovieStatusType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
