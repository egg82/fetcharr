package me.egg82.arr.radarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.radarr.v3.schema.CommandResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Command extends AbstractFetchableAPIObject {
    public static final Command UNKNOWN = new Command();

    private final PVector<@NotNull CommandResource> resources;

    public Command(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            List<@NotNull CommandResource> resourcesL = new ArrayList<>();
            for (int i = 0; i < resources.length(); i++) {
                resourcesL.add(new CommandResource(api, resources.getJSONObject(i)));
            }
            this.resources = TreePVector.from(resourcesL);
        } else {
            this.resources = TreePVector.singleton(new CommandResource(api, node.getObject()));
        }
    }

    private Command() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
        this.resources = TreePVector.empty();
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/command";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull PVector<@NotNull CommandResource> resources() {
        return resources;
    }

    public @Nullable CommandResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Command command)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, command.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "Command{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
