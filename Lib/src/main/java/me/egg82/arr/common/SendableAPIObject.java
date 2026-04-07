package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import org.jetbrains.annotations.NotNull;

public interface SendableAPIObject {
    @NotNull String apiPath();

    @NotNull JsonNode node();
}
