package me.egg82.arr.lidarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractArrAPI;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LidarrV1API extends AbstractArrAPI {
    public LidarrV1API(@NotNull String baseUrl, @NotNull String apiKey, int id) {
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
            logger.warn("LIDARR_{} returned unexpected response for URL {}: {}", id(), baseUrl + "/api", response.getObject().toString());
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.LIDARR;
    }

    @Override
    public @NotNull String version() {
        return "v1";
    }

    @Override
    public void search(int... itemIds) {
        for (int itemId : itemIds) {
            JSONObject data = new JSONObject(Map.of(
                    "artistId", itemId,
                    "name", "ArtistSearch"
            ));
            JsonNode response = post("/api/" + version() + "/command", new JsonNode(data.toString()));
            if (response == null) {
                return;
            }
            int id = NumberParser.getInt(-1, response.getObject(), "id");
            if (id < 0) {
                logger.warn("LIDARR_{} returned unexpected response for URL {}: {}", id(), baseUrl + "/api/" + version() + "/command", response.getObject().toString());
            }
        }
    }
}
