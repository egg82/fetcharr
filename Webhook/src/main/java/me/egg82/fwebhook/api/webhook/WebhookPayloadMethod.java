package me.egg82.fwebhook.api.webhook;

/**
 * Represents an HTTP method to use for a webhook payload.
 */
public enum WebhookPayloadMethod {
    /**
     * HTTP GET method
     */
    GET,
    /**
     * HTTP POST method
     */
    POST,
    /**
     * GTTP PUT method
     */
    PUT,
    /**
     * HTTP PATCH method
     */
    PATCH,
    /**
     * HTTP DELETE method
     */
    DELETE
}
