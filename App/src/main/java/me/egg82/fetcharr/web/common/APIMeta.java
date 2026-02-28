package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public class APIMeta {
    private final LocalDateTime created;

    public APIMeta() {
        this.created = LocalDateTime.now();
    }

    public APIMeta(@NotNull JSONObject obj) {
        this.created = LocalDateTime.parse(obj.getString("created"));
    }

    public @NotNull JSONObject object() {
        return new JSONObject(Map.of(
                "created", created.toString()
        ));
    }

    public @NotNull LocalDateTime created() {
        return created;
    }
}
