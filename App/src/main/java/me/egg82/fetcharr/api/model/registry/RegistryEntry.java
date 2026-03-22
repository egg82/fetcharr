package me.egg82.fetcharr.api.model.registry;

import me.egg82.fetcharr.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegistryEntry {
    private final Plugin owner;
    private final Object value;

    public RegistryEntry(@NotNull Plugin owner, @NotNull Object value) {
        this.owner = owner;
        this.value = value;
    }

    public @NotNull Plugin owner() {
        return owner;
    }

    public @NotNull Object value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegistryEntry that)) return false;
        return Objects.equals(owner, that.owner) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, value);
    }

    @Override
    public String toString() {
        return "RegistryEntry{" +
                "owner=" + owner +
                ", value=" + value +
                '}';
    }
}
