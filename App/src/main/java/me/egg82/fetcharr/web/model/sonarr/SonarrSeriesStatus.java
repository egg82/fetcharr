package me.egg82.fetcharr.web.model.sonarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SonarrSeriesStatus {
    CONTINUING("continuing"),
    ENDED("ended"),
    UPCOMING("upcoming"),
    DELETED("deleted");

    private final String apiName;
    SonarrSeriesStatus(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SonarrSeriesStatus parse(@NotNull SonarrSeriesStatus def, @Nullable String val) {
        SonarrSeriesStatus r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SonarrSeriesStatus parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SonarrSeriesStatus t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
