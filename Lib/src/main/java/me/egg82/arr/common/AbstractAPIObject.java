package me.egg82.arr.common;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAPIObject implements APIObject {
    protected final ArrAPI api;
    protected final JSONObject obj;

    public AbstractAPIObject(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        this.api = api;
        this.obj = obj;
    }

    public @NotNull JSONObject obj() {
        return new JSONObject(obj.toMap());
    }
}
