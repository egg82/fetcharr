package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CommandPriority {
    NORMAL("normal"),
    HIGH("high"),
    LOW("low");

    private final String apiName;
    CommandPriority(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull CommandPriority get(@NotNull CommandPriority def, @Nullable JSONObject obj, @Nullable String key) {
        CommandPriority r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable CommandPriority get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull CommandPriority parse(@NotNull CommandPriority def, @Nullable String val) {
        CommandPriority r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable CommandPriority parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (CommandPriority e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
