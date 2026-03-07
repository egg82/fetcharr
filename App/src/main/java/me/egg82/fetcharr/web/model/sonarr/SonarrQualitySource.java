package me.egg82.fetcharr.web.model.sonarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SonarrQualitySource {
    UNKNOWN("unknown"),
    TELEVISION("television"),
    TELEVISION_RAW("televisionRaw"),
    WEB("web"),
    WEB_RIP("webRip"),
    DVD("dvd"),
    BLURAY("bluray"),
    BLURAY_RAW("blurayRaw");

    private final String apiName;
    SonarrQualitySource(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SonarrQualitySource parse(@NotNull SonarrQualitySource def, @Nullable String val) {
        SonarrQualitySource r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SonarrQualitySource parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SonarrQualitySource t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
