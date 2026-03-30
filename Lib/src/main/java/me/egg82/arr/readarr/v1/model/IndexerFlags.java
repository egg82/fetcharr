package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum IndexerFlags {
    FREELEECH("freeleech"),
    HALFLEECH("halfleech"),
    DOUBLE_UPLOAD("doubleUpload"),
    INTERNAL("internal"),
    SCENE("scene"),
    FREELEECH_75("freeleech75"),
    FREELEECH_25("freeleech25");

    private final String apiName;
    IndexerFlags(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull IndexerFlags get(@NotNull IndexerFlags def, @Nullable JSONObject obj, @Nullable String key) {
        IndexerFlags r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable IndexerFlags get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull IndexerFlags parse(@NotNull IndexerFlags def, @Nullable String val) {
        IndexerFlags r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable IndexerFlags parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (IndexerFlags e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
