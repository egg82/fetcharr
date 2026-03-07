package me.egg82.fetcharr.env;

import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    SHORT_CACHE_TIME(TimeValue.class, "Expiration time for short-lived cached values", new TimeValue(65, TimeUnit.MINUTES)),
    LONG_CACHE_TIME(TimeValue.class, "Expiration time for long-lived cached values", new TimeValue(6L, TimeUnit.HOURS)),
    DATA_DIR(File.class, "Data storage directory", new File("/data")),
    SSL_PATH(File.class, "File path containing custom SSL certs", new File("/etc/ssl/certs/ca-bundle.crt")),
    SEARCH_AMOUNT(Integer.class, "Number of items to search at each run", 5),
    SEARCH_INTERVAL(TimeValue.class, "How often to search", new TimeValue(1L, TimeUnit.HOURS)),
    MONITORED_ONLY(Boolean.class, "True to select only monitored items, false to select all", true),
    SKIP_TAGS(String[].class, "Comma-separated list of tags to skip searching", new String[]{});

    private final Class<?> type;
    private final String description;
    private final Object def;

    <T> ConfigVars(@NotNull Class<T> type, @NotNull String description, T def) {
        this.type = type;
        this.description = description;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public @NotNull String description() {
        return this.description;
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull ConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @Nullable String get(@NotNull ConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull ConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r.split(",") : var.def();
    }

    public static @NotNull LogMode getLogMode(@NotNull ConfigVars var) {
        return LogMode.parse(var.def(), System.getenv(var.name()));
    }

    public static boolean getBool(@NotNull ConfigVars var) {
        return BooleanParser.parse(var.def(), System.getenv(var.name()));
    }

    public static int getInt(@NotNull ConfigVars var) {
        return NumberParser.parseInt(var.def(), System.getenv(var.name()));
    }

    public static long getLong(@NotNull ConfigVars var) {
        return NumberParser.parseLong(var.def(), System.getenv(var.name()));
    }

    public static float getFloat(@NotNull ConfigVars var) {
        return NumberParser.parseFloat(var.def(), System.getenv(var.name()));
    }

    public static double getDouble(@NotNull ConfigVars var) {
        return NumberParser.parseDouble(var.def(), System.getenv(var.name()));
    }

    public static @NotNull TimeValue getTimeValue(@NotNull ConfigVars var) {
        return TimeValueParser.parse(var.def(), System.getenv(var.name()));
    }

    public static @NotNull File getFile(@NotNull ConfigVars var) {
        return FileParser.parse(var.def(), System.getenv(var.name()));
    }
}
