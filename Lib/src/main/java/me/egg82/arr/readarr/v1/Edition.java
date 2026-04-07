package me.egg82.arr.readarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.readarr.v1.schema.EditionResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edition extends AbstractFetchableAPIObject {
    public static final Edition UNKNOWN = new Edition();

    private final PVector<@NotNull EditionResource> resources;

    public Edition(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            List<@NotNull EditionResource> resourcesL = new ArrayList<>();
            for (int i = 0; i < resources.length(); i++) {
                resourcesL.add(new EditionResource(api, resources.getJSONObject(i)));
            }
            this.resources = TreePVector.from(resourcesL);
        } else {
            this.resources = TreePVector.singleton(new EditionResource(api, node.getObject()));
        }
    }

    private Edition() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
        this.resources = TreePVector.empty();
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/edition";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull PVector<@NotNull EditionResource> resources() {
        return resources;
    }

    public @Nullable EditionResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edition edition)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, edition.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "Edition{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
