package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SourceType {
    TMDB("tmdb"),
    MAPPINGS("mappings"),
    USER("user"),
    INDEXER("indexer");

    private final String apiName;
    SourceType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SourceType get(@NotNull SourceType def, @Nullable JSONObject obj, @Nullable String key) {
        SourceType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable SourceType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull SourceType parse(@NotNull SourceType def, @Nullable String val) {
        SourceType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SourceType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SourceType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
