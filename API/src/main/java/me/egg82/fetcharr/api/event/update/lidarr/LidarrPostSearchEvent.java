package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.lidarr.v1.schema.CommandResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after submitting a list of {@link ArtistResource}s to the *arr app for searching
 */
public class LidarrPostSearchEvent extends AbstractUpdaterEvent {
    private final CommandResource resource;

    public LidarrPostSearchEvent(@NotNull CommandResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull CommandResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "LidarrPostSearchEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
