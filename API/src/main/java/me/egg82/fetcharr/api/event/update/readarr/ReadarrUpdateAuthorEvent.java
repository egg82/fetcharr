package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link AuthorResource} has passed all checks and is about to be added to the update queue
 */
public class ReadarrUpdateAuthorEvent extends AbstractCancellableUpdaterEvent {
    private final AuthorResource resource;

    public ReadarrUpdateAuthorEvent(@NotNull AuthorResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull AuthorResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrUpdateAuthorEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "ReadarrUpdateAuthorEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
