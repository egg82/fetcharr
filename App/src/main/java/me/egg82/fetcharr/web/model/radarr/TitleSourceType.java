package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TitleSourceType {
    TMDB("tmdb"),
    MAPPINGS("mappings"),
    USER("user"),
    INDEXER("indexer");

    private final String apiName;
    TitleSourceType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull TitleSourceType parse(@NotNull TitleSourceType def, @Nullable String val) {
        TitleSourceType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable TitleSourceType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (TitleSourceType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
