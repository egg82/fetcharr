package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;

public interface FetchableAPIObject {
    @NotNull String apiPath();

    @NotNull TimeValue cacheTime();

    @NotNull JsonNode node();
}
