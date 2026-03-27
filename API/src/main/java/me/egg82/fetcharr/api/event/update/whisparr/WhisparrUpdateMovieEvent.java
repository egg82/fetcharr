package me.egg82.fetcharr.api.event.update.whisparr;

import me.egg82.arr.whisparr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link MovieResource} has passed all checks and is about to be added to the update queue
 */
public class WhisparrUpdateMovieEvent extends AbstractCancellableUpdaterEvent {
    private final MovieResource resource;

    public WhisparrUpdateMovieEvent(@NotNull MovieResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull MovieResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WhisparrUpdateMovieEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "WhisparrUpdateMovieEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
