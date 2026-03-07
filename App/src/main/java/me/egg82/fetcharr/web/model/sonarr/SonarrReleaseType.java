package me.egg82.fetcharr.web.model.sonarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SonarrReleaseType {
    UNKNOWN("unknown"),
    SINGLE_EPISODE("singleEpisode"),
    MULTI_EPISODE("multiEpisode"),
    SEASON_PACK("seasonPack");

    private final String apiName;
    SonarrReleaseType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SonarrReleaseType parse(@NotNull SonarrReleaseType def, @Nullable String val) {
        SonarrReleaseType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SonarrReleaseType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SonarrReleaseType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
