package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CommandResult {
    UNKNOWN("unknown"),
    SUCCESSFUL("successful"),
    UNSUCCESSFUL("unsuccessful");

    private final String apiName;
    CommandResult(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull CommandResult get(@NotNull CommandResult def, @Nullable JSONObject obj, @Nullable String key) {
        CommandResult r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable CommandResult get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull CommandResult parse(@NotNull CommandResult def, @Nullable String val) {
        CommandResult r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable CommandResult parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (CommandResult e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
