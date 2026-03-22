package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin context, provided during initialization.
 */
public interface PluginContext {
    /**
     * This plugin's specific configuration
     * directory. Not automatically created.
     *
     * @return the directory for this plugin's configuration
     */
    @NotNull File configDir();
}
