package me.egg82.fetcharr.web.model.radarr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ProviderMessageType {
    INFO("info"),
    WARN("warning"),
    ERR("error");

    private final String apiName;
    ProviderMessageType(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull ProviderMessageType parse(@NotNull ProviderMessageType def, @Nullable String val) {
        ProviderMessageType r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ProviderMessageType parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (ProviderMessageType t : values()) {
            if (t.apiName.equalsIgnoreCase(val)) {
                return t;
            }
        }
        return null;
    }
}
