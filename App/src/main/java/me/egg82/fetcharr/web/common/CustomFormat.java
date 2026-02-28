package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import org.jetbrains.annotations.NotNull;

public class CustomFormat extends APIObject {
    public static final CustomFormat UNKNOWN = new CustomFormat();

    public CustomFormat(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);
    }

    private CustomFormat() {
        super(new JSONObject(), NullAPI.INSTANCE);
    }

    // public boolean unknown() { return id < 0; }
}
