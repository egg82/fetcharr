package me.egg82.fwebhook.api.webhook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PMap;

/**
 * An object representing a payload to send to a webhook URL.
 */
public interface WebhookPayload {
    /**
     * Gets the latter half of the URL path.
     *
     * <p>Usually appended to the end of the
     * "base URL" for the destination.</p>
     *
     * @return the end of the URL path
     */
    @NotNull String path();

    /**
     * Gets the HTTP content type.
     *
     * <p>eg. "application/json"</p>
     *
     * @return the HTTP content type
     */
    @Nullable String contentType();

    /**
     * Gets the HTTP accept type.
     *
     * <p>eg. "application/json"</p>
     *
     * @return the HTTP accept type
     */
    @NotNull String accept();

    /**
     * Gets headers (if any) to send to the destination.
     *
     * @return all headers to send to the destination, or null for none
     */
    @Nullable PMap<String, @NotNull String> headers();

    /**
     * Gets the body to send to the destination.
     *
     * @return the body to send to the destination
     */
    byte @NotNull [] body();

    /**
     * Gets the HTTP method to use for the destination.
     *
     * @return the HTTP method to use for the destination
     */
    @NotNull WebhookPayloadMethod method();
}
