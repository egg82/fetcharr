package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.TreePSet;

import java.util.Objects;
import java.util.Set;

public class PluginDescriptorImpl implements PluginDescriptor {
    private final String id;
    private final String name;
    private final String description;
    private final Set<@NotNull String> authors;
    private final String version;
    private final String className;
    private final Set<@NotNull String> exports;

    public PluginDescriptorImpl(@NotNull String id, @NotNull String name, @Nullable String description, @Nullable Set<@NotNull String> authors, @NotNull String version, @NotNull String className, @Nullable Set<@NotNull String> exports) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.className = className;
        this.exports = exports;
    }

    @Override
    public @NotNull String id() {
        return this.id;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @Nullable String description() {
        return this.description;
    }

    @Override
    public @Nullable PSet<@NotNull String> authors() {
        return this.authors != null ? TreePSet.from(this.authors) : null;
    }

    @Override
    public @NotNull String version() {
        return this.version;
    }

    @Override
    public @NotNull String className() {
        return this.className;
    }

    @Override
    public @Nullable PSet<@NotNull String> exports() {
        return this.exports != null ? TreePSet.from(exports) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginDescriptorImpl that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PluginDescriptorImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                ", version='" + version + '\'' +
                ", className='" + className + '\'' +
                ", exports=" + exports +
                '}';
    }
}
