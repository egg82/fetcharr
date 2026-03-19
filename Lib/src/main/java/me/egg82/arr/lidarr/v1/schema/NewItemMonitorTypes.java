package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum NewItemMonitorTypes {
    ALL("all"),
    NONE("none"),
    NEW("new");

    private final String apiName;
    NewItemMonitorTypes(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull NewItemMonitorTypes get(@NotNull NewItemMonitorTypes def, @Nullable JSONObject obj, @Nullable String key) {
        NewItemMonitorTypes r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable NewItemMonitorTypes get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull NewItemMonitorTypes parse(@NotNull NewItemMonitorTypes def, @Nullable String val) {
        NewItemMonitorTypes r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable NewItemMonitorTypes parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (NewItemMonitorTypes e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
