package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CommandTrigger {
    UNSPECIFIED("unspecified"),
    MANUAL("manual"),
    SCHEDULED("scheduled");

    private final String apiName;
    CommandTrigger(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull CommandTrigger get(@NotNull CommandTrigger def, @Nullable JSONObject obj, @Nullable String key) {
        CommandTrigger r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable CommandTrigger get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull CommandTrigger parse(@NotNull CommandTrigger def, @Nullable String val) {
        CommandTrigger r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable CommandTrigger parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (CommandTrigger e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
