package me.egg82.fetcharr.web.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReleaseStatus {
    RELEASED,
    UNKNOWN;

    public static @Nullable ReleaseStatus fromString(@NotNull String status) {
        for (ReleaseStatus v : ReleaseStatus.values()) {
            if (v.name().equalsIgnoreCase(status)) {
                return v;
            }
        }
        return null;
    }
}
