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
    void init(@NotNull PluginContext context) throws Exception;

    /**
     * Plugin start. Called on startup, after {@link #init(PluginContext)}.
     */
    void start() throws Exception;

    /**
     * Plugin stop. Called on shutdown.
     */
    void stop() throws Exception;
}
