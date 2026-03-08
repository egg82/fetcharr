package me.egg82.fetcharr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringParser {
    public static @NotNull String parse(@NotNull String def, @Nullable JSONObject node, @Nullable String key) {
        String r = parse(node, key);
        return r != null ? r : def;
    }

    public static @Nullable String parse(@Nullable JSONObject node, @Nullable String key) {
        if (node == null || key == null || node.isEmpty()) {
            return null;
        }

        return node.has(key) ? node.getString(key) : null;
    }

    private StringParser() { }
}
