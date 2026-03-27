package me.egg82.fwebhook.api;

import org.jetbrains.annotations.NotNull;

/**
 * Provides static access to the {@link WebhookAPI} instance
 */
public class WebhookAPIProvider {
    private static WebhookAPI instance = null;

    /**
     * Return the current instance of the running {@link WebhookAPI} service.
     *
     * @return The current running instance of the {@link WebhookAPI} service
     *
     * @throws IllegalStateException if not yet loaded, or unloaded
     */
    public static @NotNull WebhookAPI instance() {
        WebhookAPI r = instance;
        if (r == null) {
            throw new IllegalStateException("WebhookAPI is not loaded.");
        }
        return r;
    }

    private static void register(@NotNull WebhookAPI instance) {
        WebhookAPIProvider.instance = instance;
    }

    private static void deregister() {
        WebhookAPIProvider.instance = null;
    }

    private WebhookAPIProvider() { }
}
