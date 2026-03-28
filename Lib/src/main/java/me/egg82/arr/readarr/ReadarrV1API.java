package me.egg82.arr.readarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractArrAPI;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReadarrV1API extends AbstractArrAPI {
    public ReadarrV1API(@NotNull String baseUrl, @NotNull String apiKey, int id) {
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
            logger.warn("READARR_{} returned unexpected response for URL {}: {}", this.id, this.baseUrl + "/api", response.getObject().toString());
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.READARR;
    }

    @Override
    public @NotNull String version() {
        return "v1";
    }

    @Override
    public void search(int... itemIds) {
        JSONObject data = new JSONObject(Map.of(
                "authorIds", itemIds,
                "name", "AuthorSearch"
        ));
        JsonNode response = post("/api/" + version() + "/command", new JsonNode(data.toString()));
        if (response == null) {
            return;
        }
        int id = NumberParser.getInt(-1, response.getObject(), "id");
        if (id < 0) {
            logger.warn("READARR_{} returned unexpected response for URL {}: {}", this.id, this.baseUrl + "/api/" + version() + "/command", response.getObject().toString());
        }
    }

    @Override
    public String toString() {
        return "ReadarrV1API{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
