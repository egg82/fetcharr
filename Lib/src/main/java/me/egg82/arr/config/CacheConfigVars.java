package me.egg82.arr.config;

import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.FileParser;
import me.egg82.arr.parse.TimeValueParser;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

public enum CacheConfigVars {
    USE_FILE_CACHE(Tristate.class, Tristate.AUTO),
    USE_MEMORY_CACHE(Tristate.class, Tristate.AUTO),
    CACHE_DIR(File.class, new File("/app/cache")),
    SHORT_CACHE_TIME(TimeValue.class, new TimeValue(65, TimeUnit.MINUTES)),
    LONG_CACHE_TIME(TimeValue.class, new TimeValue(6L, TimeUnit.HOURS));

    private final Class<?> type;
    private final Object def;

    <T> CacheConfigVars(@NotNull Class<T> type, T def) {
        this.type = type;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull CacheConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @Nullable String get(@NotNull CacheConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull CacheConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r.split(",") : var.def();
    }

    public static @NotNull Tristate getTristate(@NotNull CacheConfigVars var) {
        return Tristate.parse(var.def(), System.getenv(var.name()));
    }

    public static @NotNull File getFile(@NotNull CacheConfigVars var) {
        return FileParser.parse(var.def(), System.getenv(var.name()), true);
    }

    public static @NotNull TimeValue getTimeValue(@NotNull CacheConfigVars var) {
        return TimeValueParser.parse(var.def(), System.getenv(var.name()));
    }
}
