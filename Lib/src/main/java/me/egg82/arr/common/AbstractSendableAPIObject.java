package me.egg82.arr.common;

import kong.unirest.core.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractSendableAPIObject implements SendableAPIObject {
    protected final JsonNode node;

    public AbstractSendableAPIObject(@NotNull JsonNode node) {
        this.node = node;
    }

    @Override
    public @NotNull JsonNode node() {
        return new JsonNode(node.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractSendableAPIObject that)) return false;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(node);
    }

    @Override
    public String toString() {
        return "AbstractSendableAPIObject{" +
                "node=" + node +
                '}';
    }
}
