package me.egg82.arr.lidarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.lidarr.v1.schema.IndexerResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Indexer extends AbstractFetchableAPIObject {
    public static final Indexer UNKNOWN = new Indexer();

    private final List<@NotNull IndexerResource> resources = new ArrayList<>();

    public Indexer(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            for (int i = 0; i < resources.length(); i++) {
                this.resources.add(new IndexerResource(api, resources.getJSONObject(i)));
            }
        } else {
            this.resources.add(new IndexerResource(api, node.getObject()));
        }
    }

    private Indexer() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/indexer";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull List<@NotNull IndexerResource> resources() {
        return resources;
    }

    public @Nullable IndexerResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Indexer artist)) return false;
        return Objects.equals(resources, artist.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "Indexer{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
