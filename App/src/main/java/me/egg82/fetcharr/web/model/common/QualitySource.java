package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QualitySource {
    UNKNOWN("unknown"),
    CAM("can"),
    TELESYNC("telesync"),
    TELECINE("telecine"),
    WORKPRINT("workprint"),
    TV("tv"),
    WEBDL("webdl"),
    WEBRIP("webrip"),
    TELEVISION("television"),
    TELEVISION_RAW("televisionRaw"),
    WEB("web"),
    WEB_RIP("webRip"),
    DVD("dvd"),
    BLURAY("bluray"),
    BLURAY_RAW("blurayRaw");

    private final String apiName;
    QualitySource(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull QualitySource parse(@NotNull QualitySource def, @Nullable String val) {
        QualitySource r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable QualitySource parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (QualitySource t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
