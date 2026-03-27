package me.egg82.fwebhook.internal.api.webhook;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.api.webhook.WebhookDestination;
import me.egg82.fwebhook.api.webhook.WebhookPayload;
import me.egg82.fwebhook.api.webhook.WebhookTransform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractWebhookDestination implements WebhookDestination {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final WebhookAPI api;
    protected final CommentedConfigurationNode config;

    protected final String id;
    protected final WebhookTransform transform;

    public AbstractWebhookDestination(@NotNull WebhookAPI api, @NotNull String id, @NotNull CommentedConfigurationNode config, @NotNull WebhookTransform transform) {
        this.api = api;
        this.config = config;

        this.id = id;
        this.transform = transform;
    }

    @Override
    public @NotNull String id() {
        return this.id;
    }

    @Override
    public boolean accepts(@NotNull FetcharrEvent event) {
        return transform.accepts(event);
    }

    protected final @Nullable String handleInternal(@NotNull FetcharrEvent event, @NotNull String baseUrl) throws Exception {
        WebhookPayload payload = transform.transform(event);
        if (payload == null) {
            logger.warn("Could not transform event {}", event.eventType().getName());
            return null;
        }

        String contentType = payload.contentType();
        contentType = contentType != null ? contentType : "application/json";

        return switch (payload.method()) {
            case GET -> get(baseUrl + payload.path(), payload.accept(), payload.headers());
            case PUT -> put(baseUrl + payload.path(), contentType, payload.accept(), payload.headers(), payload.body());
            case POST -> post(baseUrl + payload.path(), contentType, payload.accept(), payload.headers(), payload.body());
            case PATCH -> patch(baseUrl + payload.path(), contentType, payload.accept(), payload.headers(), payload.body());
            case DELETE -> delete(baseUrl + payload.path(), contentType, payload.accept(), payload.headers(), payload.body());
        };
    }

    protected final @Nullable String get(@NotNull String url, @NotNull String accept) {
        return get(url, accept, null);
    }

    protected final @Nullable String get(@NotNull String url, @NotNull String accept, @Nullable Map<String, @NotNull String> headers) {
        return parseResponse(Unirest.get(url)
                .accept(accept)
                .headers(headers)
                .asString());
    }

    protected final @Nullable String put(@NotNull String url, @NotNull String contentType, @NotNull String accept) {
        return put(url, contentType, accept, null, null);
    }

    protected final @Nullable String put(@NotNull String url, @NotNull String contentType, @NotNull String accept, @NotNull Map<String, @NotNull String> headers) {
        return put(url, contentType, accept, headers, null);
    }

    protected final @Nullable String put(@NotNull String url, @NotNull String contentType, @NotNull String accept, byte @NotNull [] body) {
        return put(url, contentType, accept, null, body);
    }

    protected final @Nullable String put(@NotNull String url, @NotNull String contentType, @NotNull String accept, @Nullable Map<String, @NotNull String> headers, byte @Nullable [] body) {
        return parseResponse(Unirest.put(url)
                .contentType(contentType)
                .accept(accept)
                .headers(headers)
                .body(body)
                .asString());
    }

    protected final @Nullable String post(@NotNull String url, @NotNull String contentType, @NotNull String accept) {
        return post(url, contentType, accept, null, null);
    }

    protected final @Nullable String post(@NotNull String url, @NotNull String contentType, @NotNull String accept, @NotNull Map<String, @NotNull String> headers) {
        return post(url, contentType, accept, headers, null);
    }

    protected final @Nullable String post(@NotNull String url, @NotNull String contentType, @NotNull String accept, byte @NotNull [] body) {
        return post(url, contentType, accept, null, body);
    }

    protected final @Nullable String post(@NotNull String url, @NotNull String contentType, @NotNull String accept, @Nullable Map<String, @NotNull String> headers, byte @Nullable [] body) {
        return parseResponse(Unirest.post(url)
                .contentType(contentType)
                .accept(accept)
                .headers(headers)
                .body(body)
                .asString());
    }

    protected final @Nullable String patch(@NotNull String url, @NotNull String contentType, @NotNull String accept) {
        return patch(url, contentType, accept, null, null);
    }

    protected final @Nullable String patch(@NotNull String url, @NotNull String contentType, @NotNull String accept, @NotNull Map<String, @NotNull String> headers) {
        return patch(url, contentType, accept, headers, null);
    }

    protected final @Nullable String patch(@NotNull String url, @NotNull String contentType, @NotNull String accept, byte @NotNull [] body) {
        return patch(url, contentType, accept, null, body);
    }

    protected final @Nullable String patch(@NotNull String url, @NotNull String contentType, @NotNull String accept, @Nullable Map<String, @NotNull String> headers, byte @Nullable [] body) {
        return parseResponse(Unirest.patch(url)
                .contentType(contentType)
                .accept(accept)
                .headers(headers)
                .body(body)
                .asString());
    }

    protected final @Nullable String delete(@NotNull String url, @NotNull String contentType, @NotNull String accept) {
        return delete(url, contentType, accept, null, null);
    }

    protected final @Nullable String delete(@NotNull String url, @NotNull String contentType, @NotNull String accept, @NotNull Map<String, @NotNull String> headers) {
        return delete(url, contentType, accept, headers, null);
    }

    protected final @Nullable String delete(@NotNull String url, @NotNull String contentType, @NotNull String accept, byte @NotNull [] body) {
        return delete(url, contentType, accept, null, body);
    }

    protected final @Nullable String delete(@NotNull String url, @NotNull String contentType, @NotNull String accept, @Nullable Map<String, @NotNull String> headers, byte @Nullable [] body) {
        return parseResponse(Unirest.delete(url)
                .contentType(contentType)
                .accept(accept)
                .headers(headers)
                .body(body)
                .asString());
    }

    private @Nullable String parseResponse(@NotNull HttpResponse<String> response) {
        if (!response.isSuccess()) {
            logger.warn("Got non-success response (code {}) for URL {}: {}", response.getStatus(), response.getRequestSummary().getUrl(), response.getBody());
            response.getParsingError().ifPresent(v -> logger.warn("Parsing error for URL {}", response.getRequestSummary().getUrl(), v));
            return null;
        }

        if (response.getBody() == null) {
            logger.warn("Body was null for URL {}", response.getRequestSummary().getUrl());
        }

        return response.getBody();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractWebhookDestination that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AbstractWebhookDestination{" +
                "api=" + api +
                ", config=" + config +
                ", id='" + id + '\'' +
                ", transform=" + transform +
                '}';
    }
}
