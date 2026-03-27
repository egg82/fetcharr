package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fired before submitting a list of {@link ArtistResource}s to the *arr app for searching
 */
public class LidarrSearchEvent extends AbstractCancellableUpdaterEvent {
    private List<ArtistResource> resources;

    public LidarrSearchEvent(@NotNull List<@NotNull ArtistResource> resources, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = new ArrayList<>(resources);
    }

    public @NotNull List<@NotNull ArtistResource> resources() {
        return resources;
    }

    public void resources(@NotNull List<@NotNull ArtistResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "LidarrSearchEvent{" +
                "resources=" + resources +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
