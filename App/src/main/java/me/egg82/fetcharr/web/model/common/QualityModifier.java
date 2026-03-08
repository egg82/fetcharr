package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QualityModifier {
    NONE("none"),
    REGIONAL("regional"),
    SCREENER("screener"),
    RAWHD("rawhd"),
    BRDISK("brdisk"),
    REMUX("remux");

    private final String apiName;
    QualityModifier(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull QualityModifier parse(@NotNull QualityModifier def, @Nullable String val) {
        QualityModifier r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable QualityModifier parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (QualityModifier t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
