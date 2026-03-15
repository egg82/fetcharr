package me.egg82.arr.common;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface APIObject {
    @NotNull JSONObject obj();
}
