package me.egg82.arr.readarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractSendableAPIObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AuthorSearchCommand extends AbstractSendableAPIObject {
    public AuthorSearchCommand(int id) {
        super(new JsonNode(new JSONObject(Map.of("name", "AuthorSearch", "authorId", id)).toString()));
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/command";
    }

    @Override
    public String toString() {
        return "AuthorSearchCommand{" +
                "node=" + node +
                '}';
    }
}
