package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RatingType {
    USER("user"),
    CRITIC("critic");

    private final String apiName;
    RatingType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull RatingType parse(@NotNull RatingType def, @Nullable String val) {
        RatingType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable RatingType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (RatingType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
