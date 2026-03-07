package me.egg82.fetcharr.web.model.sonarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SonarrSeriesType {
    STANDARD("standard"),
    DAILY("daily"),
    ANIME("anime");

    private final String apiName;
    SonarrSeriesType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SonarrSeriesType parse(@NotNull SonarrSeriesType def, @Nullable String val) {
        SonarrSeriesType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SonarrSeriesType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SonarrSeriesType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
