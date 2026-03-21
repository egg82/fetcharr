package me.egg82.fetcharr.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.InvalidPathException;

public enum LogConfigVars {
    LOG_MODE(LogMode.class, LogMode.INFO),
    LOG_DIR(File.class, new File("/app/logs"));

    private final Class<?> type;
    private final Object def;

    <T> LogConfigVars(@NotNull Class<T> type, T def) {
        this.type = type;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull LogConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @NotNull LogMode getLogMode(@NotNull LogConfigVars var) {
        return LogMode.parse(var.def(), System.getenv(var.name()));
    }

    public static @NotNull File getFile(@NotNull LogConfigVars var) {
        return parseFile(var.def(), System.getenv(var.name()));
    }

    private static @NotNull File parseFile(@NotNull File def, @Nullable String val) {
        File r = parseFile(val);
        return r != null ? r : def;
    }

    private static @Nullable File parseFile(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        File r = new File(val);
        try {
            r.toPath();
            return r;
        } catch (InvalidPathException ignored) { // Special case - can't load any class using loggers here
            return null;
        }
    }
}
