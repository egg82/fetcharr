package me.egg82.arr.sonarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractArrAPI;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.sonarr.v3.SeriesSearchCommand;
import me.egg82.arr.sonarr.v3.schema.CommandResource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SonarrV3API extends AbstractArrAPI {
    public SonarrV3API(@NotNull String baseUrl, @NotNull String apiKey, int id) {
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
            logger.warn("SONARR_{} returned unexpected response for URL {}: {}", this.id, this.baseUrl + "/api", response.getObject().toString());
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.SONARR;
    }

    @Override
    public @NotNull String version() {
        return "v3";
    }

    @Override
    public void search(int... itemIds) {
        for (int itemId : itemIds) {
            CommandResource response = send(new SeriesSearchCommand(itemId), CommandResource.class);
            if (response == null) {
                continue;
            }
            if (response.id() < 0) {
                logger.warn("SONARR_{} returned unexpected response for URL {}: {}", this.id, this.baseUrl + "/api/" + version() + "/command", response);
            }
        }
    }

    @Override
    public String toString() {
        return "SonarrV3API{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
