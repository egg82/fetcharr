package me.egg82.fwebhook.api;

import me.egg82.fwebhook.api.webhook.WebhookDestination;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;

/**
 * The webhook API.
 *
 * <p>The API allows plugins to register
 * and unregister webhooks destinations,
 * which allow for creating custom webhooks
 * and removing/replacing webhooks that
 * already exist.</p>
 */
public interface WebhookAPI {
    /**
     * Registers a webhook handler.
     *
     * @param destination the webhook handler to register
     * @return true if registration was successful, false if not
     */
    boolean register(@NotNull WebhookDestination destination);

    /**
     * Unregisters a webhook handler.
     *
     * @param destination the handler to unregister
     * @return true if removal was successful, false if not
     */
    boolean unregister(@NotNull WebhookDestination destination);

    /**
     * Gets a list of currently-registered webhook handlers.
     *
     * @return a list of currently-registered webhook handlers
     */
    @NotNull PVector<@NotNull WebhookDestination> destinations();
}
