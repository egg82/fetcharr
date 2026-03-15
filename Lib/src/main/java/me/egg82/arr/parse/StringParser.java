package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringParser {
    public static @NotNull String get(@NotNull String def, @Nullable JSONObject obj, @Nullable String key) {
        String r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable String get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || obj.isEmpty()) {
            return null;
        }

        return obj.has(key) && obj.get(key) != null ? obj.getString(key) : null;
    }

    private StringParser() { }
}
