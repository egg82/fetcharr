package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class PluginContextImpl implements PluginContext {
    private final File dataDir;
    private final File configDir;
    private final File pluginJarFile;

    public PluginContextImpl(@NotNull File dataDir, @NotNull File configDir, @NotNull File pluginJarFile) {
        this.dataDir = dataDir;
        this.configDir = configDir;
        this.pluginJarFile = pluginJarFile;
    }

    @Override
    public @NotNull File dataDir() {
        return this.dataDir;
    }

    @Override
    public @NotNull File configDir() {
        return this.configDir;
    }

    @Override
    public @NotNull File pluginJarFile() {
        return this.pluginJarFile;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginContextImpl that)) return false;
        return Objects.equals(pluginJarFile, that.pluginJarFile);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pluginJarFile);
    }

    @Override
    public String toString() {
        return "PluginContextImpl{" +
                "dataDir=" + dataDir +
                ", configDir=" + configDir +
                ", pluginJarFile=" + pluginJarFile +
                '}';
    }
}
