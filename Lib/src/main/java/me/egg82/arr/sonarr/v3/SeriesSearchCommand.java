package me.egg82.arr.sonarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractSendableAPIObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SeriesSearchCommand extends AbstractSendableAPIObject {
    public SeriesSearchCommand(int id) {
        super(new JsonNode(new JSONObject(Map.of("name", "SeriesSearch", "seriesId", id)).toString()));
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/command";
    }

    @Override
    public String toString() {
        return "SeriesSearchCommand{" +
                "node=" + node +
                '}';
    }
}
