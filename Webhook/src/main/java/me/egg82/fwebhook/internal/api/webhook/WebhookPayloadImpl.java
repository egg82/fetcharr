package me.egg82.fwebhook.internal.api.webhook;

import me.egg82.fwebhook.api.webhook.WebhookPayload;
import me.egg82.fwebhook.api.webhook.WebhookPayloadMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PMap;
import org.pcollections.TreePMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class WebhookPayloadImpl implements WebhookPayload {
    private final String path;
    private final String contentType;
    private final String accept;
    private final PMap<String, @NotNull String> headers;
    private final byte[] body;
    private final WebhookPayloadMethod method;

    public WebhookPayloadImpl(@NotNull String path, @Nullable String contentType, @NotNull String accept, @Nullable Map<String, @NotNull String> headers, byte @NotNull [] body, @NotNull WebhookPayloadMethod method) {
        this.path = path;
        this.contentType = contentType;
        this.accept = accept;
        this.headers = headers != null ? TreePMap.from(headers) : null;
        this.body = body;
        this.method = method;
    }

    @Override
    public @NotNull String path() {
        return this.path;
    }

    @Override
    public @Nullable String contentType() {
        return this.contentType;
    }

    @Override
    public @NotNull String accept() {
        return this.accept;
    }

    @Override
    public @Nullable PMap<String, @NotNull String> headers() {
        return this.headers;
    }

    @Override
    public byte @NotNull [] body() {
        return this.body;
    }

    @Override
    public @NotNull WebhookPayloadMethod method() {
        return this.method;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebhookPayloadImpl that)) return false;
        return Objects.equals(path, that.path) && Objects.equals(contentType, that.contentType) && Objects.equals(accept, that.accept) && Objects.equals(headers, that.headers) && Objects.deepEquals(body, that.body) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, contentType, accept, headers, Arrays.hashCode(body), method);
    }

    @Override
    public String toString() {
        return "WebhookPayloadImpl{" +
                "path='" + path + '\'' +
                ", contentType='" + contentType + '\'' +
                ", accept='" + accept + '\'' +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", method=" + method +
                '}';
    }
}
