package me.egg82.arr.readarr.v1;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.arr.common.AbstractFetchableAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.NullArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.readarr.v1.schema.BookFileResource;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookFile extends AbstractFetchableAPIObject {
    public static final BookFile UNKNOWN = new BookFile();

    private final PVector<@NotNull BookFileResource> resources;

    public BookFile(@NotNull ArrAPI api, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        super(api, node, lastFetched);

        if (node.isArray()) {
            JSONArray resources = node.getArray();
            List<@NotNull BookFileResource> resourcesL = new ArrayList<>();
            for (int i = 0; i < resources.length(); i++) {
                resourcesL.add(new BookFileResource(api, resources.getJSONObject(i)));
            }
            this.resources = TreePVector.from(resourcesL);
        } else {
            this.resources = TreePVector.singleton(new BookFileResource(api, node.getObject()));
        }
    }

    private BookFile() {
        super(NullArrAPI.INSTANCE, new JsonNode("{}"), Instant.EPOCH);
        this.resources = TreePVector.empty();
    }

    @Override
    public @NotNull String apiPath() {
        return "/api/v1/bookfile";
    }

    @Override
    public @NotNull TimeValue cacheTime() {
        return CacheConfigVars.getTimeValue(CacheConfigVars.SHORT_CACHE_TIME);
    }

    public @NotNull PVector<@NotNull BookFileResource> resources() {
        return resources;
    }

    public @Nullable BookFileResource resource() {
        return resources.size() == 1 ? resources.getFirst() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookFile book)) return false;
        return Objects.equals(resources, book.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resources);
    }

    @Override
    public String toString() {
        return "BookFile{" +
                "resources=" + resources +
                ", api=" + api +
                ", node=" + node +
                '}';
    }
}
