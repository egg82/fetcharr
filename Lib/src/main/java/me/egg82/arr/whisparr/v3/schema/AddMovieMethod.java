package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AddMovieMethod {
    MANUAL("manual"),
    LIST("list"),
    COLLECTION("collection");

    private final String apiName;
    AddMovieMethod(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull AddMovieMethod get(@NotNull AddMovieMethod def, @Nullable JSONObject obj, @Nullable String key) {
        AddMovieMethod r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable AddMovieMethod get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull AddMovieMethod parse(@NotNull AddMovieMethod def, @Nullable String val) {
        AddMovieMethod r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable AddMovieMethod parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (AddMovieMethod e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
