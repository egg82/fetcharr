package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PluginLoadException extends RuntimeException {
    private final File jarFile;

    public PluginLoadException(@NotNull File jarFile, @NotNull String message, @NotNull Throwable cause) {
        super(message, cause);

        this.jarFile = jarFile;
    }

    public PluginLoadException(@NotNull File jarFile, @NotNull String message) {
        super(message);

        this.jarFile = jarFile;
    }

    public @NotNull File jarFile() {
        return jarFile;
    }

    @Override
    public String toString() {
        return "PluginLoadException{" +
                "jarFile=" + jarFile +
                '}';
    }
}
