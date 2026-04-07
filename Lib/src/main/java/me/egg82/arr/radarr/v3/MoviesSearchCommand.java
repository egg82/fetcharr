package me.egg82.arr.radarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractSendableAPIObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MoviesSearchCommand extends AbstractSendableAPIObject {
    public MoviesSearchCommand(int @NotNull [] ids) {
        super(new JsonNode(new JSONObject(Map.of("name", "MoviesSearch", "movieIds", ids)).toString()));
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/command";
    }

    @Override
    public String toString() {
        return "MoviesSearchCommand{" +
                "node=" + node +
                '}';
    }
}
