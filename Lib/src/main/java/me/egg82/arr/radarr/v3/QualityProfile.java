package me.egg82.arr.radarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.radarr.v3.schema.QualityProfileResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QualityProfile extends AbstractFetchableAPIObject {
    public static final QualityProfile UNKNOWN = new QualityProfile();

    private final List<@NotNull QualityProfileResource> resources = new ArrayList<>();

    public QualityProfile(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            for (int i = 0; i < resources.length(); i++) {
                this.resources.add(new QualityProfileResource(api, resources.getJSONObject(i)));
            }
        } else {
            this.resources.add(new QualityProfileResource(api, node.getObject()));
        }
    }

    private QualityProfile() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/qualityprofile";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.LONG_CACHE_TIME);
    }

    public @NotNull List<@NotNull QualityProfileResource> resources() {
        return resources;
    }

    public @Nullable QualityProfileResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfile that)) return false;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "QualityProfile{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
