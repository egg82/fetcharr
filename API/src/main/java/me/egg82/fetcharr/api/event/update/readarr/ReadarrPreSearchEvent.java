package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fired before submitting a list of {@link AuthorResource}s to the *arr app for searching
 */
public class ReadarrPreSearchEvent extends AbstractCancellableUpdaterEvent {
    private List<AuthorResource> resources;

    public ReadarrPreSearchEvent(@NotNull List<@NotNull AuthorResource> resources, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = new ArrayList<>(resources);
    }

    public @NotNull List<@NotNull AuthorResource> resources() {
        return resources;
    }

    public void resources(@NotNull List<@NotNull AuthorResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrPreSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "ReadarrPreSearchEvent{" +
                "resources=" + resources +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
