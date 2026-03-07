package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ScanType {
    PROGRESSIVE,
    INTERLACED;

    public static @NotNull ScanType parse(@NotNull ScanType def, @Nullable String val) {
        ScanType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ScanType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (ScanType t : values()) {
            if (t.name().equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
