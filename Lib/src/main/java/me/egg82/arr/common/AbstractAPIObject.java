package me.egg82.arr.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.config.APIConfigVars;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class AbstractAPIObject implements APIObject {
    protected final ArrAPI api;
    protected final JSONObject obj;

    public AbstractAPIObject(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        this.api = api;
        this.obj = APIConfigVars.getBool(APIConfigVars.PROVIDE_RAW_API_OBJ) ? obj : null;
    }

    public @NotNull JSONObject obj() {
        return new JSONObject(obj != null ? obj.toMap() : Map.of());
    }
}
