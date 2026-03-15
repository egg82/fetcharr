package me.egg82.arr.radarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractArrAPI;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RadarrV3API extends AbstractArrAPI {
    public RadarrV3API(@NotNull String baseUrl, @NotNull String apiKey, int id) {
        super(baseUrl, apiKey, id);
    }

    @Override
    public boolean valid() {
        JsonNode response = get("/api");
        if (response == null) {
            return false;
        }
        String current = response.getObject().getString("current");
        if (current == null || !current.equalsIgnoreCase(version())) {
            logger.warn("RADARR_{} returned unexpected response for URL {}: {}", id(), baseUrl + "/api", response.getObject().toString());
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.RADARR;
    }

    @Override
    public @NotNull String version() {
        return "v3";
    }

    @Override
    public void search(int... itemIds) {
        JSONObject data = new JSONObject(Map.of(
                "movieIds", itemIds,
                "name", "MoviesSearch"
        ));
        JsonNode response = post("/api/" + version() + "/command", new JsonNode(data.toString()));
        if (response == null) {
            return;
        }
        int id = NumberParser.getInt(-1, response.getObject(), "id");
        if (id < 0) {
            logger.warn("RADARR_{} returned unexpected response for URL {}: {}", id(), baseUrl + "/api/" + version() + "/command", response.getObject().toString());
        }
    }
}
