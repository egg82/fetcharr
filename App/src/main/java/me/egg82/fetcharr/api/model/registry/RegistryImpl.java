package me.egg82.fetcharr.api.model.registry;

import me.egg82.fetcharr.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RegistryImpl implements Registry {
    // ChatGPT cleaned some of the original code for this up

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConcurrentMap<@NotNull Class<?>, @NotNull List<@NotNull RegistryEntry>> entries = new ConcurrentHashMap<>();

    public RegistryImpl() { }

    @Override
    public <T> void register(@NotNull Plugin owner, @NotNull Class<T> type, @NonNull T value) {
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "value of type " + value.getClass().getName() +
                            " is not an instance of " + type.getName()
            );
        }

        entries.compute(type, (k, v) -> {
            List<RegistryEntry> list = (v != null) ? new ArrayList<>(v) : new ArrayList<>();

            for (RegistryEntry entry : list) {
                if (entry.owner() == owner && entry.value() == value) {
                    logger.debug("Not registering duplicate {}", type.getName());
                    return List.copyOf(list);
                }
            }

            logger.debug("Registering {}", type.getName());
            list.add(new RegistryEntry(owner, value));
            return List.copyOf(list);
        });
    }

    @Override
    public @Nullable <T> T getFirst(@NotNull Class<T> type) {
        List<RegistryEntry> v = entries.get(type);
        if (v == null || v.isEmpty()) {
            return null;
        }
        return type.cast(v.getFirst().value());
    }

    @Override
    public @NotNull <T> PVector<T> getAll(@NotNull Class<T> type) {
        List<RegistryEntry> v = entries.get(type);
        if (v == null || v.isEmpty()) {
            return TreePVector.empty();
        }

        List<T> r = new ArrayList<>(v.size());
        for (RegistryEntry entry : v) {
            r.add(type.cast(entry.value()));
        }
        return TreePVector.from(r);
    }

    @Override
    public void unregister(@NotNull Plugin owner, @NotNull Class<?> type) {
        entries.computeIfPresent(type, (k, v) -> {
            List<RegistryEntry> filtered = v.stream()
                    .filter(entry -> entry.owner() != owner)
                    .toList();

            if (filtered.size() != v.size()) {
                logger.debug("Unregistering {}", type.getName());
            }

            return filtered.isEmpty() ? null : List.copyOf(filtered);
        });
    }

    @Override
    public void unregisterAll(@NotNull Plugin owner) {
        for (Class<?> type : List.copyOf(entries.keySet())) {
            entries.computeIfPresent(type, (k, v) -> {
                List<RegistryEntry> filtered = v.stream()
                        .filter(entry -> entry.owner() != owner)
                        .toList();

                return filtered.isEmpty() ? null : List.copyOf(filtered);
            });
        }
    }

    @Override
    public String toString() {
        return "RegistryImpl{" +
                "entries=" + entries +
                '}';
    }
}
