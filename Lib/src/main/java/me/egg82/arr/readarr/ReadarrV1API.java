package me.egg82.arr.readarr;

import kong.unirest.core.JsonNode;
import me.egg82.arr.common.AbstractArrAPI;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.lidarr.v1.ArtistSearchCommand;
import me.egg82.arr.readarr.v1.schema.CommandResource;
import org.jetbrains.annotations.NotNull;

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
        for (int itemId : itemIds) {
            CommandResource response = send(new ArtistSearchCommand(itemId), CommandResource.class);
            if (response == null) {
                continue;
            }
            if (response.id() < 0) {
                logger.warn("READARR_{} returned unexpected response for URL {}: {}", this.id, this.baseUrl + "/api/" + version() + "/command", response);
            }
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
