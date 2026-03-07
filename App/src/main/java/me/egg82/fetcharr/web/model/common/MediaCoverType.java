package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MediaCoverType {
    UNKNOWN("unknown"),
    POSTER("poster"),
    BANNER("banner"),
    FAN_ART("fanart"),
    SCREENSHOT("screenshot"),
    HEADSHOT("headshot"),
    CLEAR_LOGO("clearlogo");

    private final String apiName;
    MediaCoverType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull MediaCoverType parse(@NotNull MediaCoverType def, @Nullable String val) {
        MediaCoverType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable MediaCoverType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (MediaCoverType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
