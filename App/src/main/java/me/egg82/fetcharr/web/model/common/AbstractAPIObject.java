package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.ArrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;

public abstract class AbstractAPIObject<T extends AbstractAPIObject<T>> implements APIObject<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ArrAPI api;
    protected final String apiPath;

    protected Instant fetched = null;

    protected AbstractAPIObject(@NotNull ArrAPI api, @NotNull String apiPath) {
        this.api = api;
        this.apiPath = apiPath;
    }

    protected abstract void parse(@NotNull JsonNode data);

    protected final @NotNull JSONFile cacheFile() {
        return new JSONFile(new File(getBasePath(), "base.json"));
    }

    protected final @NotNull JSONFile cacheFile(int id) {
        return new JSONFile(new File(getBasePath(), id + ".json"));
    }

    protected final @NotNull JSONFile metaFile() {
        return new JSONFile(new File(getBasePath(), "base.meta.json"));
    }

    protected final @NotNull JSONFile metaFile(int id) {
        return new JSONFile(new File(getBasePath(), id + ".meta.json"));
    }

    private @NotNull File getBasePath() {
        File base = ConfigVars.getFile(ConfigVars.DATA_DIR);
        File arr = new File(base, api.type().name().toLowerCase() + "-" + api.id());
        return new File(arr, getClass().getSimpleName());
    }

    protected final @NotNull String url() {
        return api.baseUrl() + apiPath;
    }

    protected final @Nullable JsonNode get(@NotNull String apiKey) {
        return parseResponse(Unirest.get(api.baseUrl() + apiPath)
                .header("X-Api-Key", apiKey)
                .accept("application/json")
                .asJson());
    }

    protected @Nullable JsonNode post(@NotNull String apiKey) {
        return post(apiKey, null);
    }

    protected @Nullable JsonNode post(@NotNull String apiKey, @Nullable JsonNode body) {
        return parseResponse(Unirest.post(api.baseUrl() + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    protected @Nullable JsonNode put(@NotNull String apiKey) {
        return put(apiKey, null);
    }

    protected @Nullable JsonNode put(@NotNull String apiKey, @Nullable JsonNode body) {
        return parseResponse(Unirest.put(api.baseUrl() + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    protected @Nullable JsonNode delete(@NotNull String apiKey) {
        return delete(apiKey, null);
    }

    protected @Nullable JsonNode delete(@NotNull String apiKey, @Nullable JsonNode body) {
        return parseResponse(Unirest.put(api.baseUrl() + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
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
            logger.warn("JSON body was null for URL {}", response.getRequestSummary().getUrl());
        }

        return response.getBody();
    }
}
