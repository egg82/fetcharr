package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PluginManifestEntry {
    private final String url;
    private final String filename;
    private final String sha256;

    public PluginManifestEntry(@NotNull String url, @NotNull String filename, @Nullable String sha256) {
        this.url = url;
        this.filename = filename;
        this.sha256 = sha256;
    }

    public @NotNull String url() {
        return url;
    }

    public @NotNull String filename() {
        return filename;
    }

    public @Nullable String sha256() {
        return sha256;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginManifestEntry that)) return false;
        return Objects.equals(url, that.url) && Objects.equals(filename, that.filename) && Objects.equals(sha256, that.sha256);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, filename, sha256);
    }

    @Override
    public String toString() {
        return "PluginManifestEntry{" +
                "url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                ", sha256='" + sha256 + '\'' +
                '}';
    }
}
