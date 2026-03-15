package me.egg82.arr.radarr.v3;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.radarr.v3.schema.TagResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tag extends AbstractFetchableAPIObject {
    private final List<@NotNull TagResource> resources = new ArrayList<>();

    public Tag(@NotNull ArrAPI api, @NotNull JsonNode node) {
        super(api, node);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            for (int i = 0; i < resources.length(); i++) {
                this.resources.add(new TagResource(api, resources.getJSONObject(i)));
            }
        } else {
            this.resources.add(new TagResource(api, node.getObject()));
        }
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v3/tag";
    }

    @Override
    public @NotNull Duration cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.LONG_CACHE_TIME).duration();
    }

    public @NotNull List<@NotNull TagResource> resources() {
        return resources;
    }

    public @Nullable TagResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag tag)) return false;
        return Objects.equals(resources, tag.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
