package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Modifier {
    NONE("none"),
    REGIONAL("regional"),
    SCREENER("screener"),
    RAW_HD("rawhd"),
    BR_DISK("brdisk"),
    REMUX("remux");

    private final String apiName;
    Modifier(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull Modifier get(@NotNull Modifier def, @Nullable JSONObject obj, @Nullable String key) {
        Modifier r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable Modifier get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull Modifier parse(@NotNull Modifier def, @Nullable String val) {
        Modifier r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable Modifier parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (Modifier e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
