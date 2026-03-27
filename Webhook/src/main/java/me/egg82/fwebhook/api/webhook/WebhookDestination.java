package me.egg82.fwebhook.api.webhook;

import me.egg82.fetcharr.api.event.FetcharrEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An object representing a webhook destination for Fetcharr events.
 */
public interface WebhookDestination {
    /**
     * Gets the unique ID for this webhook destination.
     *
     * @return a unique ID for this webhook destination
     */
    @NotNull String id();

    /**
     * Gets the name of this webhook type.
     *
     * @return the type of this webhook
     */
    @NotNull String type();

    /**
     * Attempts to handle the event.
     *
     * <p>Returns true if this destination was capable
     * of and configured to handle the event type.
     * Returns false if this destination was either not
     * capable or not configured to handle this type
     * of event.</p>
     *
     * @param event the event to handle
     * @return true if the event was handled, false if not
     * @throws Exception if the event threw an exception during handling
     */
    boolean handle(@NotNull FetcharrEvent event) throws Exception;
}
