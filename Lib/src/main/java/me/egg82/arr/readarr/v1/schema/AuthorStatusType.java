package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AuthorStatusType {
    CONTINUING("continuing"),
    ENDED("ended");

    private final String apiName;
    AuthorStatusType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull AuthorStatusType get(@NotNull AuthorStatusType def, @Nullable JSONObject obj, @Nullable String key) {
        AuthorStatusType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable AuthorStatusType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull AuthorStatusType parse(@NotNull AuthorStatusType def, @Nullable String val) {
        AuthorStatusType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable AuthorStatusType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (AuthorStatusType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
