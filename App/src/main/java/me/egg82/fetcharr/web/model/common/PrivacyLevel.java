package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PrivacyLevel {
    NORMAL("normal"),
    PASSWORD("password"),
    API_KEY("apiKey"),
    USER_NAME("userName");

    private final String apiName;
    PrivacyLevel(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return this.apiName;
    }

    public static @NotNull PrivacyLevel parse(@NotNull PrivacyLevel def, @Nullable String val) {
        PrivacyLevel r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable PrivacyLevel parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (PrivacyLevel t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
