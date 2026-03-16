package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MediaCoverTypes {
    UNKNOWN("unknown"),
    POSTER("poster"),
    BANNER("banner"),
    FANART("fanart"),
    SCREENSHOT("screenshot"),
    HEADSHOT("headshot"),
    CLEAR_LOGO("clearlogo");

    private final String apiName;
    MediaCoverTypes(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull MediaCoverTypes get(@NotNull MediaCoverTypes def, @Nullable JSONObject obj, @Nullable String key) {
        MediaCoverTypes r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable MediaCoverTypes get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull MediaCoverTypes parse(@NotNull MediaCoverTypes def, @Nullable String val) {
        MediaCoverTypes r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable MediaCoverTypes parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (MediaCoverTypes e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
