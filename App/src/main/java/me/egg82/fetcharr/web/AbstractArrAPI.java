package me.egg82.fetcharr.web;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractArrAPI implements ArrAPI {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String baseUrl;
    protected final String apiKey;
    private final int id;

    public AbstractArrAPI(@NotNull String baseUrl, @NotNull String apiKey, int id) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.id = id;
    }

    public int id() {
        return id;
    }

    public @NotNull String baseUrl() {
        return this.baseUrl;
    }

    protected @Nullable JsonNode get(@NotNull String apiPath) {
        return get(apiPath, null);
    }

    protected final @Nullable JsonNode get(@NotNull String apiPath, @Nullable Map<String, @NotNull Object> params) {
        return parseResponse(Unirest.get(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .accept("application/json")
                .queryString(params)
                .asJson());
    }

    protected @Nullable JsonNode post(@NotNull String apiPath) {
        return post(apiPath, null);
    }

    protected @Nullable JsonNode post(@NotNull String apiPath, @Nullable JsonNode body) {
        return parseResponse(Unirest.post(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    private @Nullable JsonNode parseResponse(@NotNull HttpResponse<JsonNode> response) {
        if (!response.isSuccess()) {
            logger.debug("Got non-success response (code {}) for URL {}", response.getStatus(), response.getRequestSummary().getUrl());
            response.getParsingError().ifPresent(v -> logger.debug("JSON parsing error for URL {}", response.getRequestSummary().getUrl(), v));
            return null;
        }

        if (response.getBody() == null) {
            logger.debug("JSON body was null for URL {}", response.getRequestSummary().getUrl());
        }

        return response.getBody();
    }
}
