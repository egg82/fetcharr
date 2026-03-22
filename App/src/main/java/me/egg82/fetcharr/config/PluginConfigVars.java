package me.egg82.fetcharr.config;

import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.FileParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public enum PluginConfigVars {
    PLUGIN_DIR(File.class, new File("/app/plugins"));

    private final Class<?> type;
    private final Object def;

    <T> PluginConfigVars(@NotNull Class<T> type, T def) {
        this.type = type;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull PluginConfigVars var) {
        return System.getenv(var.name()) != null;
    }

    public static @NotNull File getFile(@NotNull CacheConfigVars var) {
        return FileParser.parse(var.def(), System.getenv(var.name()), true);
    }
}
