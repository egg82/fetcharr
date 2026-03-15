package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFetchableAPIObject implements FetchableAPIObject {
    protected final ArrAPI api;
    protected final JsonNode node;

    public AbstractFetchableAPIObject(@NotNull ArrAPI api, @NotNull JsonNode node) {
        this.api = api;
        this.node = node;
    }

    @Override
    public @NotNull JsonNode node() {
        return new JsonNode(node.toString());
    }
}
