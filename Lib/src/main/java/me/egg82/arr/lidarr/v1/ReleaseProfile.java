package me.egg82.arr.lidarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.lidarr.v1.schema.ReleaseProfileResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReleaseProfile extends AbstractFetchableAPIObject {
    public static final ReleaseProfile UNKNOWN = new ReleaseProfile();

    private final List<@NotNull ReleaseProfileResource> resources = new ArrayList<>();

    public ReleaseProfile(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            for (int i = 0; i < resources.length(); i++) {
                this.resources.add(new ReleaseProfileResource(api, resources.getJSONObject(i)));
            }
        } else {
            this.resources.add(new ReleaseProfileResource(api, node.getObject()));
        }
    }

    private ReleaseProfile() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/releaseprofile";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull List<@NotNull ReleaseProfileResource> resources() {
        return resources;
    }

    public @Nullable ReleaseProfileResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReleaseProfile artist)) return false;
        return Objects.equals(resources, artist.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "ReleaseProfile{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
