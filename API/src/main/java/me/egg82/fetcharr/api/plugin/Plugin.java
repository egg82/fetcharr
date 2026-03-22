package me.egg82.fetcharr.api.plugin;

import org.jetbrains.annotations.NotNull;

/**
 * A plugin instance. All plugins implement this interface.
 */
public interface Plugin {
    /**
     * Plugin initialization. Called on startup, before {@link #start()}.
     *
     * @param context Any context the plugin may need for initialization.
     */
    void init(@NotNull PluginContext context);

    /**
     * Plugin start. Called on startup, after initialization.
     */
    void start();

    /**
     * Plugin stop. Called on shutdown.
     */
    void stop();
}
