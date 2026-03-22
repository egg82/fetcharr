package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link ArtistResource} has passed all checks and is about to be added to the update queue
 */
public class LidarrUpdateArtistEvent extends AbstractCancellableUpdaterEvent {
    private final ArtistResource resource;

    public LidarrUpdateArtistEvent(@NotNull ArtistResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull ArtistResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrUpdateArtistEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "LidarrUpdateArtistEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
