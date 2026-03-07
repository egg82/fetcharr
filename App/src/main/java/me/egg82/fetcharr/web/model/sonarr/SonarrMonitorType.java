package me.egg82.fetcharr.web.model.sonarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SonarrMonitorType {
    UNKNOWN("unknown"),
    ALL("all"),
    FUTURE("future"),
    MISSING("missing"),
    EXISTING("existing"),
    FIRST_SEASON("firstSeason"),
    LAST_SEASON("lastSeason"),
    LATEST_SEASON("latestSeason"),
    PILOT("pilot"),
    RECENT("recent"),
    MONITOR_SPECIALS("monitorSpecials"),
    UNMONITOR_SPECIALS("unmonitorSpecials"),
    NONE("none"),
    SKIP("skip");

    private final String apiName;
    SonarrMonitorType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull SonarrMonitorType parse(@NotNull SonarrMonitorType def, @Nullable String val) {
        SonarrMonitorType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable SonarrMonitorType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (SonarrMonitorType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
