package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AlbumAddType {
    AUTOMATIC("automatic"),
    MANUAL("manual");

    private final String apiName;
    AlbumAddType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull AlbumAddType get(@NotNull AlbumAddType def, @Nullable JSONObject obj, @Nullable String key) {
        AlbumAddType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable AlbumAddType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull AlbumAddType parse(@NotNull AlbumAddType def, @Nullable String val) {
        AlbumAddType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable AlbumAddType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (AlbumAddType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
