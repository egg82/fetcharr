package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProviderMessage extends AbstractAPIObject {
    private final String message;
    private final ProviderMessageType type;

    public ProviderMessage(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.message = StringParser.get(obj, "message");
        this.type = ProviderMessageType.get(ProviderMessageType.INFO, obj, "type");
    }

    public @Nullable String message() {
        return message;
    }

    public @NotNull ProviderMessageType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProviderMessage that)) return false;
        return Objects.equals(message, that.message) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type);
    }

    @Override
    public String toString() {
        return "ProviderMessage{" +
                "message='" + message + '\'' +
                ", type=" + type +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
