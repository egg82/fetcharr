package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public interface FetchableAPIObject {
    @NotNull String apiPath();

    @NotNull TimeValue cacheTime();
    @NotNull Instant lastFetched();
    @NotNull Duration expiresIn();
    boolean expired();

    @NotNull JsonNode node();
}
