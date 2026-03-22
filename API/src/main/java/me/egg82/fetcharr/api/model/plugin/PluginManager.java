package me.egg82.fetcharr.api.model.plugin;

import me.egg82.fetcharr.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;

/**
 * Represents the object responsible for managing {@link Plugin} instances.
 */
public interface PluginManager {
    /**
     * Gets the current list of {@link EnabledPlugin}s
     *
     * @return the current list of enabled plugins
     */
    @NotNull PVector<@NotNull EnabledPlugin> plugins();

    /**
     * Shut each {@link Plugin} down. Calls
     * {@link Plugin#stop()} and closes their
     * respective {@link ClassLoader}s
     */
    void shutdown();
}
