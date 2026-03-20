package me.egg82.arr.sonarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.sonarr.v3.schema.EpisodeResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Episode extends AbstractFetchableAPIObject {
    public static final Episode UNKNOWN = new Episode();

    private final PVector<@NotNull EpisodeResource> resources;

    public Episode(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            List<@NotNull EpisodeResource> resourcesL = new ArrayList<>();
            for (int i = 0; i < resources.length(); i++) {
                resourcesL.add(new EpisodeResource(api, resources.getJSONObject(i)));
            }
            this.resources = TreePVector.from(resourcesL);
        } else {
            this.resources = TreePVector.singleton(new EpisodeResource(api, node.getObject()));
        }
    }

    private Episode() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
        this.resources = TreePVector.empty();
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/episode";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull PVector<@NotNull EpisodeResource> resources() {
        return resources;
    }

    public @Nullable EpisodeResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Episode series)) return false;
        return Objects.equals(resources, series.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "Episode{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
