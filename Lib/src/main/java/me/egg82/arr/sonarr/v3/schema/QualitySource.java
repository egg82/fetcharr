package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QualitySource {
    UNKNOWN("unknown"),
    TELEVISION("television"),
    TELEVISION_RAW("televisionRaw"),
    WEB("web"),
    WEB_RIP("webRip"),
    DVD("dvd"),
    BLUERAY("bluray"),
    BLURAY_RAW("blurayRaw");

    private final String apiName;
    QualitySource(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull QualitySource get(@NotNull QualitySource def, @Nullable JSONObject obj, @Nullable String key) {
        QualitySource r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable QualitySource get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull QualitySource parse(@NotNull QualitySource def, @Nullable String val) {
        QualitySource r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable QualitySource parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (QualitySource e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
