package me.egg82.fetcharr.web.model.lidarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AlbumAddType {
    AUTOMATIC("automatic"),
    MANUAL("manual");

    private final String apiName;
    AlbumAddType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull AlbumAddType parse(@NotNull AlbumAddType def, @Nullable String val) {
        AlbumAddType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable AlbumAddType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (AlbumAddType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
