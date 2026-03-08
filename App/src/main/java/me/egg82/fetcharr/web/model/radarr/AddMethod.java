package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AddMethod {
    MANUAL("manual"),
    LIST("list"),
    COLLECTION("collection");

    private final String apiName;
    AddMethod(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull AddMethod parse(@NotNull AddMethod def, @Nullable String val) {
        AddMethod r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable AddMethod parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (AddMethod t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
