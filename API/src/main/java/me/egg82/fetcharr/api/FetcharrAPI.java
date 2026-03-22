package me.egg82.fetcharr.api;

import com.sasorio.event.bus.EventBus;
import com.sasorio.event.registry.EventRegistry;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.model.plugin.PluginManager;
import me.egg82.fetcharr.api.model.registry.Registry;
import me.egg82.fetcharr.api.model.update.UpdateManager;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

/**
 * The Fetcharr API.
 *
 * <p>The API allows plugins to read and modify Fetcharr
 * data, change behavior of the app, listen to certain events,
 * and integrate Fetcharr into other systems.</p>
 *
 * <p>This interface represents the base of the API package.
 * All functions are accessed via this interface.</p>
 *
 * <p>To start using the API, you need to obtain an instance
 * of this interface. These are registered by Fetcharr.</p>
 *
 * <p>An instance can be obtained from the static singleton
 * accessor in {@link FetcharrAPIProvider}.</p>
 */
public interface FetcharrAPI {
    /**
     * Gets the main Fetcharr {@link EventRegistry},
     * where all {@link FetcharrEvent} API events pass through.
     *
     * @return the Fetcharr event registry
     */
    @NotNull EventRegistry<@NotNull FetcharrEvent> events();

    /**
     * Gets the main Fetcharr {@link EventBus},
     * where all {@link FetcharrEvent} API events are posted.
     *
     * @return the Fetcharr event bus
     */
    @NotNull EventBus<@NotNull FetcharrEvent> bus();

    /**
     * Gets the {@link UpdateManager}, responsible for
     * managing the *arr updaters.
     *
     * <p>This manager can be used to register
     * and hook into {@link Updater} instances.</p>
     *
     * @return the current update manager
     */
    @NotNull UpdateManager updateManager();

    /**
     * Sets the {@link UpdateManager} instance.
     *
     * @param manager the update manager
     */
    void updateManager(@NotNull UpdateManager manager);

    /**
     * Gets the {@link PluginManager}, responsible for
     * managing plugins.
     *
     * <p>This manager can be used to view
     * all currently-enabled plugins.</p>
     *
     * @return the current plugin manager
     */
    @NotNull PluginManager pluginManager();

    /**
     * Gets the {@link Registry}, responsible
     * for managing the plugin API registry.
     *
     * <p>This is useful for plugins registering
     * their own API objects to be used by other
     * plugins.</p>
     *
     * @return the registry
     */
    @NotNull Registry registry();
}
