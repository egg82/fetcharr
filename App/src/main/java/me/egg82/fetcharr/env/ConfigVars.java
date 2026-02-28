package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigVars {
    LOG_MODE(String.class, "Logging mode", false),
    PROXY_HOST(String.class, "HTTP proxy host", false),
    PROXY_PORT(Integer.class, "HTTP proxy port", false),
    CONNECT_TIMEOUT(Integer.class, "HTTP connection timeout in milliseconds", false),
    REQUEST_TIMEOUT(Integer.class, "HTTP request timeout in milliseconds", false),
    CONNECT_TTL(Integer.class, "HTTP connection TTL in milliseconds", false),
    VERIFY_CERTS(Boolean.class, "Verify SSL certificates", false);

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigVars.class);

    private final Class<?> type;
    private final String description;
    private final boolean required;

    ConfigVars(@NotNull Class<?> type, @NotNull String description, boolean required) {
        this.type = type;
        this.description = description;
        this.required = required;
    }

    public @NotNull Class<?> type() { return this.type; }

    public @NotNull String description() { return this.description; }

    public boolean required() { return this.required; }

    public static boolean hasVar(@NotNull ConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @Nullable String getVar(@NotNull ConfigVars var) {
        return System.getenv(var.name());
    }

    public static @NotNull String getVar(@NotNull ConfigVars var, @NotNull String def) {
        String val = System.getenv(var.name());
        return val != null ? val : def;
    }

    public static int getVar(@NotNull ConfigVars var, int def) {
        return toInt(var, def);
    }

    private static int toInt(@NotNull ConfigVars var, int def) {
        String v = System.getenv(var.name());
        if (v == null) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ignored) { }
        LOGGER.warn("Could not translate environment variable {} to integer: {}", var.name(), System.getenv(var.name()));
        return def;
    }

    private static int toIntInternal(@NotNull String var, int def) {
        try {
            return Integer.parseInt(var);
        } catch (NumberFormatException ignored) { }
        return def;
    }

    public static boolean getVar(@NotNull ConfigVars var, boolean def) {
        return toBool(var, def);
    }

    private static boolean toBool(@NotNull ConfigVars var, boolean def) {
        String v = System.getenv(var.name());
        if (v == null) {
            return def;
        }

        v = v.strip();
        int i = toIntInternal(v, -1);

        if (
                v.equalsIgnoreCase("true")
                || v.equalsIgnoreCase("yes")
                || i > 0
        ) {
            return true;
        }

        if (
                v.equalsIgnoreCase("false")
                || v.equalsIgnoreCase("no")
                || i == 0
        ) {
            return false;
        }

        LOGGER.warn("Could not translate environment variable {} to boolean: {}", var.name(), System.getenv(var.name()));
        return def;
    }
}
