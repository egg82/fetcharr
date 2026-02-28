package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RadarrConfigVars {
    RADARR_URL("RADARR_{}_URL"),
    RADARR_API_KEY("RADARR_{}_API_KEY");

    private final String name;

    RadarrConfigVars(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String name(int num) {
        return this.name.replace("{}", String.valueOf(num));
    }

    public static boolean hasVar(@NotNull RadarrConfigVars var, int num) {
        return System.getenv(var.name(num)) != null;
    }

    public static @Nullable String getVar(@NotNull RadarrConfigVars var, int num) {
        return System.getenv(var.name(num));
    }

    public static @NotNull String getVar(@NotNull RadarrConfigVars var, int num, @NotNull String def) {
        String val = System.getenv(var.name(num));
        return val != null ? val : def;
    }
}
