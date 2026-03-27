package me.egg82.fwebhook.api.webhook;

import me.egg82.fetcharr.api.event.FetcharrEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object representing a webhook event transform.
 */
public interface WebhookTransform {
    /**
     * Returns true if this transform is capable
     * of accepting this event type. Returns false
     * if this transform is not capable of accepting
     * this event type.
     *
     * @param event the event to check against
     * @return true if this transform can accept the event type, false if not
     */
    boolean accepts(@NotNull FetcharrEvent event);

    /**
     * Attempts to transform an event into a payload.
     *
     * @param event the event to transform
     * @return a payload, if the transform was successful. Null if it was not successful
     * @throws Exception if an exception was thrown during event transformation
     */
    @Nullable WebhookPayload transform(@NotNull FetcharrEvent event) throws Exception;
}
