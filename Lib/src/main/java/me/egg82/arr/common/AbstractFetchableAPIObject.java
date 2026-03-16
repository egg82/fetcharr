package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public abstract class AbstractFetchableAPIObject implements FetchableAPIObject {
    protected final ArrAPI api;
    protected final JsonNode node;

    private final Instant lastFetched;

    public AbstractFetchableAPIObject(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        this.api = api;
        this.node = node;
        this.lastFetched = lastFetched;
    }

    @Override
    public @NotNull JsonNode node() {
        return new JsonNode(node.toString());
    }

    @Override
    public @NotNull Instant lastFetched() {
        return lastFetched;
    }

    @Override
    public boolean expired() {
        return Instant.now().isAfter(lastFetched.plus(cacheTime().duration()));
    }

    @Override
    public @NotNull Duration expiresIn() {
        return Duration.between(Instant.now(), lastFetched.plus(cacheTime().duration()));
    }
}
