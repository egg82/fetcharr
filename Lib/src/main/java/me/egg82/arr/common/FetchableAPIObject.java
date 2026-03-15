package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface FetchableAPIObject {
    @NotNull String apiPath();

    @NotNull Duration cacheTime();

    @NotNull JsonNode node();
}
