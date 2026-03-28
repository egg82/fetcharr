package me.egg82.fetcharr.config;

import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.FileParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.TimeValueParser;
import me.egg82.arr.unit.TimeValue;
import me.egg82.fetcharr.api.model.update.MissingStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

public enum CommonConfigVars {
    PROXY_HOST(String.class, null),
    PROXY_PORT(Integer.class, 80),
    CONNECT_TIMEOUT(Integer.class, 2500),
    REQUEST_TIMEOUT(Integer.class, 120000),
    CONNECT_TTL(Integer.class, 300000),
    VERIFY_CERTS(Boolean.class, true),
    CONFIG_DIR(File.class, new File("/app/config")),
    SSL_PATH(File.class, new File("/etc/ssl/certs/ca-bundle.crt")),
    SEARCH_AMOUNT(Integer.class, 5),
    SEARCH_INTERVAL(TimeValue.class, new TimeValue(1L, TimeUnit.HOURS)),
    MONITORED_ONLY(Boolean.class, true),
    @Deprecated
    MISSING_ONLY(Boolean.class, false),
    MISSING_STATUS(me.egg82.fetcharr.api.model.update.MissingStatus.class, MissingStatus.ALL),
    SKIP_TAGS(String[].class, new String[]{}),
    USE_CUTOFF(Boolean.class, false),
    DRY_RUN(Boolean.class, false);

    private final Class<?> type;
    private final Object def;

    <T> CommonConfigVars(@NotNull Class<T> type, T def) {
        this.type = type;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull CommonConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @Nullable String get(@NotNull CommonConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull CommonConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r.split(",") : var.def();
    }

    public static boolean getBool(@NotNull CommonConfigVars var) {
        return BooleanParser.parse(var.def(), System.getenv(var.name()));
    }

    public static int getInt(@NotNull CommonConfigVars var) {
        return NumberParser.parseInt(var.def(), System.getenv(var.name()));
    }

    public static long getLong(@NotNull CommonConfigVars var) {
        return NumberParser.parseLong(var.def(), System.getenv(var.name()));
    }

    public static float getFloat(@NotNull CommonConfigVars var) {
        return NumberParser.parseFloat(var.def(), System.getenv(var.name()));
    }

    public static double getDouble(@NotNull CommonConfigVars var) {
        return NumberParser.parseDouble(var.def(), System.getenv(var.name()));
    }

    public static @NotNull TimeValue getTimeValue(@NotNull CommonConfigVars var) {
        return TimeValueParser.parse(var.def(), System.getenv(var.name()));
    }

    public static @NotNull File getFile(@NotNull CommonConfigVars var) {
        return FileParser.parse(var.def(), System.getenv(var.name()));
    }

    public static @NotNull me.egg82.fetcharr.api.model.update.MissingStatus getMissingStatus(@NotNull CommonConfigVars var) {
        return me.egg82.fetcharr.api.model.update.MissingStatus.parse(var.def(), System.getenv(var.name()));
    }
}
