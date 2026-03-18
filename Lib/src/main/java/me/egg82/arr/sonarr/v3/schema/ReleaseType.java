package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReleaseType {
    UNKNOWN("unknown"),
    SINGLE_EPISODE("singleEpisode"),
    MULTI_EPISODE("multiEpisode"),
    SEASON_PACK("seasonPack");

    private final String apiName;
    ReleaseType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull ReleaseType get(@NotNull ReleaseType def, @Nullable JSONObject obj, @Nullable String key) {
        ReleaseType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable ReleaseType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull ReleaseType parse(@NotNull ReleaseType def, @Nullable String val) {
        ReleaseType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ReleaseType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (ReleaseType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
