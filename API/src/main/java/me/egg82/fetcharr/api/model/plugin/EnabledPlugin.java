package me.egg82.fetcharr.api.model.plugin;

import me.egg82.fetcharr.api.plugin.Plugin;
import me.egg82.fetcharr.api.plugin.PluginContext;
import me.egg82.fetcharr.api.plugin.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an enabled {@link Plugin} and its associated data.
 */
public interface EnabledPlugin {
    /**
     * Gets the plugin
     *
     * @return the plugin
     */
    @NotNull Plugin plugin();

    /**
     * Gets the plugin descriptor
     *
     * @return the plugin descriptor
     */
    @NotNull PluginDescriptor descriptor();

    /**
     * Gets the plugin context
     *
     * @return the plugin context
     */
    @NotNull PluginContext context();
}
