package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CommandStatus {
    QUEUED("queued"),
    STARTED("started"),
    COMPLETED("completed"),
    FAILED("failed"),
    ABORTED("aborted"),
    CANCELLED("cancelled"),
    ORPHANED("orphaned");

    private final String apiName;
    CommandStatus(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull CommandStatus get(@NotNull CommandStatus def, @Nullable JSONObject obj, @Nullable String key) {
        CommandStatus r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable CommandStatus get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull CommandStatus parse(@NotNull CommandStatus def, @Nullable String val) {
        CommandStatus r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable CommandStatus parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (CommandStatus e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
