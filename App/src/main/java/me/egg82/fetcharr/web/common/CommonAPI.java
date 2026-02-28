package me.egg82.fetcharr.web.common;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.web.ArrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonAPI implements ArrAPI {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String url;
    private final String key;

    public CommonAPI(@NotNull String url, @NotNull String key) {
        this.url = url;
        this.key = key;
    }

    protected @Nullable JsonNode get(@NotNull String path) {
        return parseResponse(Unirest.get(url + path)
                .header("X-Api-Key", key)
                .asJson());
    }

    protected @Nullable JsonNode post(@NotNull String path) {
        return post(path, null);
    }

    protected @Nullable JsonNode post(@NotNull String path, @Nullable JsonNode body) {
        return parseResponse(Unirest.post(url + path)
                .header("X-Api-Key", key)
                .body(body)
                .asJson());
    }

    protected @Nullable JsonNode put(@NotNull String path) {
        return put(path, null);
    }

    protected @Nullable JsonNode put(@NotNull String path, @Nullable JsonNode body) {
        return parseResponse(Unirest.put(url + path)
                .header("X-Api-Key", key)
                .body(body)
                .asJson());
    }

    protected @Nullable JsonNode delete(@NotNull String path) {
        return delete(path, null);
    }

    protected @Nullable JsonNode delete(@NotNull String path, @Nullable JsonNode body) {
        return parseResponse(Unirest.put(url + path)
                .header("X-Api-Key", key)
                .body(body)
                .asJson());
    }

    private @Nullable JsonNode parseResponse(@NotNull HttpResponse<JsonNode> response) {
        if (!response.isSuccess()) {
            logger.warn("Got non-success response (code {}) for URL {}", response.getStatus(), response.getRequestSummary().getUrl());
            response.getParsingError().ifPresent(v -> {
                logger.warn("JSON parsing error for URL {}", response.getRequestSummary().getUrl(), v);
            });
            return null;
        }

        if (response.getBody() == null) {
            logger.warn("JSON body was null for URL {}", url);
        }

        return response.getBody();
    }
}
