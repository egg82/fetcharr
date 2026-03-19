package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArtistStatusType {
    CONTINUING("continuing"),
    ENDED("ended"),
    DELETED("deleted");

    private final String apiName;
    ArtistStatusType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull ArtistStatusType get(@NotNull ArtistStatusType def, @Nullable JSONObject obj, @Nullable String key) {
        ArtistStatusType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable ArtistStatusType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull ArtistStatusType parse(@NotNull ArtistStatusType def, @Nullable String val) {
        ArtistStatusType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ArtistStatusType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (ArtistStatusType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
