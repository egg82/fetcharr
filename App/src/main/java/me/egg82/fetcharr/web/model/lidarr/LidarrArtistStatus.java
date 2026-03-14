package me.egg82.fetcharr.web.model.lidarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LidarrArtistStatus {
    CONTINUING("continuing"),
    ENDED("ended"),
    DELETED("deleted");

    private final String apiName;
    LidarrArtistStatus(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull LidarrArtistStatus parse(@NotNull LidarrArtistStatus def, @Nullable String val) {
        LidarrArtistStatus r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable LidarrArtistStatus parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (LidarrArtistStatus t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
