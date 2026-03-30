package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BookAddType {
    AUTOMATIC("automatic"),
    MANUAL("manual");

    private final String apiName;
    BookAddType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull BookAddType get(@NotNull BookAddType def, @Nullable JSONObject obj, @Nullable String key) {
        BookAddType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable BookAddType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull BookAddType parse(@NotNull BookAddType def, @Nullable String val) {
        BookAddType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable BookAddType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (BookAddType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
