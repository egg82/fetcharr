package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin context, provided during initialization.
 */
public interface PluginContext {
    /**
     * This plugin's specific data directory.
     *
     * @return the directory for this plugin's data
     */
    @NotNull File dataDir();

    /**
     * This plugin's specific configuration
     * directory.
     *
     * @return the directory for this plugin's configuration
     */
    @NotNull File configDir();

    /**
     * Gets the jar file that contains the plugin.
     *
     * @return the jar file of the plugin
     */
    @NotNull File pluginJarFile();
}
