package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ProviderMessageType {
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    private final String apiName;
    ProviderMessageType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull ProviderMessageType get(@NotNull ProviderMessageType def, @Nullable JSONObject obj, @Nullable String key) {
        ProviderMessageType r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable ProviderMessageType get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull ProviderMessageType parse(@NotNull ProviderMessageType def, @Nullable String val) {
        ProviderMessageType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ProviderMessageType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (ProviderMessageType e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
