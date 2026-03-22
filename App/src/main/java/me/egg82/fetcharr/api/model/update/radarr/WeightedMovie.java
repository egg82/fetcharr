package me.egg82.fetcharr.api.model.update.radarr;

import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.fetcharr.util.Weighted;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public class WeightedMovie implements Weighted {
    private final MovieResource resource;

    private Instant lastSelected = Instant.EPOCH;

    public WeightedMovie(@NotNull MovieResource resource) {
        this.resource = resource;
    }

    @Override
    public @NotNull Instant lastUpdated() {
        return resource.lastSearchTime();
    }

    @Override
    public @NotNull Instant lastSelected() {
        return this.lastSelected;
    }

    @Override
    public void lastSelectedNow() {
        this.lastSelected = Instant.now();
    }

    public @NotNull MovieResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeightedMovie that)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(lastSelected, that.lastSelected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, lastSelected);
    }

    @Override
    public String toString() {
        return "WeightedMovie{" +
                "resource=" + resource +
                ", lastSelected=" + lastSelected +
                '}';
    }
}
