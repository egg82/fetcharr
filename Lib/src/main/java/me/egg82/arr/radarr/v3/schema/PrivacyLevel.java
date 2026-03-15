package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PrivacyLevel {
    NORMAL("normal"),
    PASSWORD("password"),
    API_KEY("apiKey"),
    USERNAME("userName");

    private final String apiName;
    PrivacyLevel(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull PrivacyLevel get(@NotNull PrivacyLevel def, @Nullable JSONObject obj, @Nullable String key) {
        PrivacyLevel r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable PrivacyLevel get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull PrivacyLevel parse(@NotNull PrivacyLevel def, @Nullable String val) {
        PrivacyLevel r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable PrivacyLevel parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (PrivacyLevel e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
