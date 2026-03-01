package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public enum ConfigVars {
    LOG_MODE(LogMode.class, "Logging mode", LogMode.INFO),
    PROXY_HOST(String.class, "HTTP proxy host", null),
    PROXY_PORT(Integer.class, "HTTP proxy port", 0),
    CONNECT_TIMEOUT(Integer.class, "HTTP connection timeout in milliseconds", 0),
    REQUEST_TIMEOUT(Integer.class, "HTTP request timeout in milliseconds", 0),
    CONNECT_TTL(Integer.class, "HTTP connection TTL in milliseconds", -1),
    VERIFY_CERTS(Boolean.class, "Verify SSL certificates", true),
    USE_CACHE(Boolean.class, "Use internal caching mechanisms", true),
    SHORT_CACHE_TIME(ParsedTime.class, "Expiration time for short-lived cached values", new ParsedTime(65, TimeUnit.MINUTES)),
    LONG_CACHE_TIME(ParsedTime.class, "Expiration time for long-lived cached values", new ParsedTime(6L, TimeUnit.HOURS)),
    DATA_DIR(File.class, "Data storage directory", new File("/data")),
    SSL_PATH(File.class, "File path containing custom SSL certs", new File("/etc/ssl/certs/ca-bundle.crt")),
    SEARCH_AMOUNT(Integer.class, "Number of items to search at each run", 5),
    SEARCH_INTERVAL(ParsedTime.class, "How often to search", new ParsedTime(1L, TimeUnit.HOURS)),
    MONITORED_ONLY(Boolean.class, "True to select only monitored items, false to select all", true);

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigVars.class);

    private final Class<?> type;
    private final String description;
    private final Object def;

    <T> ConfigVars(@NotNull Class<T> type, @NotNull String description, T def) {
        this.type = type;
        this.description = description;
        this.def = def;
    }

    public @NotNull Class<?> type() { return this.type; }

    public @NotNull String description() { return this.description; }

    public <T> T def() { return (T) this.def; }

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

    public static @NotNull LogMode getVar(@NotNull ConfigVars var, @NotNull LogMode def) {
        return LogMode.getMode(System.getenv(var.name()), def);
    }

    public static @NotNull File getVar(@NotNull ConfigVars var, @NotNull File def) {
        String val = System.getenv(var.name());
        return val != null ? new File(val) : def;
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
        } catch (NumberFormatException ignored) {
            LOGGER.warn("Could not transform environment variable {} to integer: {}", var.name(), System.getenv(var.name()));
        }
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

        LOGGER.warn("Could not transform environment variable {} to boolean: {}", var.name(), System.getenv(var.name()));
        return def;
    }

    public static @NotNull ParsedTime getVar(@NotNull ConfigVars var, @NotNull ParsedTime def) {
        return toParsedTime(var, def);
    }

    private static @NotNull ParsedTime toParsedTime(@NotNull ConfigVars var, @NotNull ParsedTime def) {
        String v = System.getenv(var.name());
        if (v == null) {
            return def;
        }
        try {
            return new ParsedTime(v);
        } catch (TimeFormatException ignored) {
            LOGGER.warn("Could not transform environment variable {} to time: {}", var.name(), System.getenv(var.name()));
        }
        return def;
    }
}
