package me.egg82.fetcharr.web.model.lidarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LidarrMonitorType {
    UNKNOWN("unknown"),
    ALL("all"),
    FUTURE("future"),
    MISSING("missing"),
    EXISTING("existing"),
    LATEST("latest"),
    FIRST("first"),
    NEW("new"),
    NONE("none");

    private final String apiName;
    LidarrMonitorType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull LidarrMonitorType parse(@NotNull LidarrMonitorType def, @Nullable String val) {
        LidarrMonitorType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable LidarrMonitorType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (LidarrMonitorType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
