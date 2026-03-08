package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RadarrMovieStatus {
    TBA("tba"),
    ANNOUNCED("announced"),
    IN_CINEMAS("inCinemas"),
    RELEASED("released"),
    DELETED("deleted");

    private final String apiName;
    RadarrMovieStatus(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull RadarrMovieStatus parse(@NotNull RadarrMovieStatus def, @Nullable String val) {
        RadarrMovieStatus r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable RadarrMovieStatus parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (RadarrMovieStatus t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
