package me.egg82.fetcharr.api.model.registry;

import me.egg82.fetcharr.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;

/**
 * Represents an object responsible for managing registry values.
 *
 * <p>The registry is a central repository for plugins
 * to register API objects and instances for other
 * plugins to make use of.</p>
 *
 * <p>This system, combined with the exports
 * list in the plugin.yaml, means that one plugin
 * can provide an API for another plugin to use.</p>
 */
public interface Registry {
    /**
     * Register a new object with its accompanying class key
     *
     * @param owner the plugin owner for the object
     * @param type the object class type (searchable key)
     * @param value the object
     * @param <T> the object type
     */
    <T> void register(@NotNull Plugin owner, @NotNull Class<T> type, @NotNull T value);

    /**
     * Gets the first object registered (if any) for
     * the supplied class key
     *
     * @param type the object class key / type
     * @return the first object registered with this class key, if any
     * @param <T> the object type
     */
    <T> @Nullable T getFirst(@NotNull Class<T> type);

    /**
     * Gets all objects registered (if any) for
     * the supplied class key
     *
     * @param type the object class key / type
     * @return all objects registered with this class key
     * @param <T> the object type
     */
    <T> @NotNull PVector<@NotNull T> getAll(@NotNull Class<T> type);

    /**
     * Unregister an existing object by the
     * supplied class key
     *
     * @param owner the plugin owner for the object
     * @param type the object type
     */
    void unregister(@NotNull Plugin owner, @NotNull Class<?> type);

    /**
     * Unregister all objects registered by a plugin
     *
     * @param owner the plugin owner for the objects
     */
    void unregisterAll(@NotNull Plugin owner);
}
