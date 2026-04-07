package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.lidarr.v1.schema.CommandResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Collection;
import java.util.Objects;

/**
 * Fired after submitting a list of {@link ArtistResource}s to the *arr app for searching
 */
public class LidarrPostSearchEvent extends AbstractUpdaterEvent {
    private final PVector<@NotNull ArtistResource> resources;
    private final PVector<@NotNull CommandResource> results;

    public LidarrPostSearchEvent(@NotNull Collection<@NotNull ArtistResource> resources, @NotNull Collection<@NotNull CommandResource> results, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = TreePVector.from(resources);
        this.results = TreePVector.from(results);
    }

    public @NotNull PVector<@NotNull ArtistResource> resources() {
        return resources;
    }

    public @NotNull PVector<@NotNull CommandResource> results() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources) && Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources, results);
    }

    @Override
    public String toString() {
        return "LidarrPostSearchEvent{" +
                "resources=" + resources +
                ", results=" + results +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
