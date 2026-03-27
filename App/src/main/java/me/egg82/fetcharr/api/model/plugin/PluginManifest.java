package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginManifest {
    private final List<@NotNull PluginManifestEntry> direct = new ArrayList<>();
    private final List<@NotNull PluginManifest> manifests = new ArrayList<>();

    public PluginManifest() { }

    public @NotNull List<@NotNull PluginManifestEntry> direct() {
        return direct;
    }

    public @NotNull List<@NotNull PluginManifest> manifests() {
        return manifests;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginManifest that)) return false;
        return Objects.equals(direct, that.direct) && Objects.equals(manifests, that.manifests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direct, manifests);
    }

    @Override
    public String toString() {
        return "PluginManifest{" +
                "direct=" + direct +
                ", manifests=" + manifests +
                '}';
    }
}
