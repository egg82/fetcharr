package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;

/**
 * Plugin descriptor, containing information about a plugin.
 */
public interface PluginDescriptor {
    /**
     * Gets the unique plugin ID
     *
     * @return the unique plugin ID
     */
    @NotNull String id();

    /**
     * Gets the plugin name
     *
     * @return the plugin name
     */
    @NotNull String name();

    /**
     * Gets the plugin description
     *
     * @return the plugin description
     */
    @Nullable String description();

    /**
     * Gets the plugin authors
     *
     * @return the plugin authors
     */
    @Nullable PSet<@NotNull String> authors();

    /**
     * Gets the plugin version
     *
     * @return the plugin version
     */
    @NotNull String version();

    /**
     * Gets the name of the main plugin
     * class, for loading.
     *
     * @return the plugin class name
     */
    @NotNull String className();

    /**
     * Gets the plugin exports
     *
     * @return the plugin exports
     */
    @Nullable PSet<@NotNull String> exports();
}
