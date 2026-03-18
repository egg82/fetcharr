package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RatingType {
    USER("user"),
    CRITIC("critic");

    private final String apiName;
    RatingType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull RatingType get(@NotNull RatingType def, @Nullable JSONObject obj, @Nullable String key) {
        RatingType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable RatingType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull RatingType parse(@NotNull RatingType def, @Nullable String val) {
        RatingType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable RatingType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (RatingType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
