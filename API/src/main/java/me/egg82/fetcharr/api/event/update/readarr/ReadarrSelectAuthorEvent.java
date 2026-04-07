package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link AuthorResource} is selected for updating, before any other checks are done
 */
public class ReadarrSelectAuthorEvent extends AbstractCancellableUpdaterEvent {
    private final AuthorResource resource;

    public ReadarrSelectAuthorEvent(@NotNull AuthorResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull AuthorResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrSelectAuthorEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "ReadarrSelectAuthorEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
