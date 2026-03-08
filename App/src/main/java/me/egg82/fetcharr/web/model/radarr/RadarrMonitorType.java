package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RadarrMonitorType {
    MOVIE_ONLY("movieOnly"),
    MOVIE_AND_COLLECTION("movieAndCollection"),
    NONE("none");

    private final String apiName;
    RadarrMonitorType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull RadarrMonitorType parse(@NotNull RadarrMonitorType def, @Nullable String val) {
        RadarrMonitorType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable RadarrMonitorType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (RadarrMonitorType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
