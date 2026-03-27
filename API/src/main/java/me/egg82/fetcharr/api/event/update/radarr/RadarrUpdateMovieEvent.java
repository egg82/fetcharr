package me.egg82.fetcharr.api.event.update.radarr;

import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link MovieResource} has passed all checks and is about to be added to the update queue
 */
public class RadarrUpdateMovieEvent extends AbstractCancellableUpdaterEvent {
    private final MovieResource resource;

    public RadarrUpdateMovieEvent(@NotNull MovieResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull MovieResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadarrUpdateMovieEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "RadarrUpdateMovieEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
