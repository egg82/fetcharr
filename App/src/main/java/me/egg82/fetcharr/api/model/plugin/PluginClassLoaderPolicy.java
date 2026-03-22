package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PluginClassLoaderPolicy {
    private final Set<@NotNull String> prefixes = new HashSet<>();

    public PluginClassLoaderPolicy(@NotNull Collection<@NotNull String> prefixes) {
        this.prefixes.addAll(prefixes);
    }

    public boolean isParentFirst(@NotNull String className) {
        for (String p : prefixes) {
            if (className.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginClassLoaderPolicy that)) return false;
        return Objects.equals(prefixes, that.prefixes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prefixes);
    }

    @Override
    public String toString() {
        return "PluginClassLoaderPolicy{" +
                "prefixes=" + prefixes +
                '}';
    }
}
