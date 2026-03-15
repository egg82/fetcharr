package me.egg82.arr.config;

import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.FileParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public enum LogConfigVars {
    LOG_DIR(File.class, new File("/log")),
    USE_STDOUT(Boolean.class, true),
    CLEAN_STDOUT(Boolean.class, false),
    USE_LOG_FILES(Boolean.class, true);

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

    public static @Nullable String get(@NotNull LogConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull LogConfigVars var) {
        String r = System.getenv(var.name());
        return r != null ? r.split(",") : var.def();
    }

    public static @NotNull File getFile(@NotNull LogConfigVars var) {
        return FileParser.parse(var.def(), System.getenv(var.name()), true);
    }

    public static boolean getBool(@NotNull LogConfigVars var) {
        return BooleanParser.parse(var.def(), System.getenv(var.name()), true);
    }
}
