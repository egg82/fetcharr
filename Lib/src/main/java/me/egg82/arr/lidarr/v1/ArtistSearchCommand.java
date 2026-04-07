package me.egg82.arr.lidarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractSendableAPIObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ArtistSearchCommand extends AbstractSendableAPIObject {
    public ArtistSearchCommand(int id) {
        super(new JsonNode(new JSONObject(Map.of("name", "ArtistSearch", "artistId", id)).toString()));
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/command";
    }

    @Override
    public String toString() {
        return "ArtistSearchCommand{" +
                "node=" + node +
                '}';
    }
}
