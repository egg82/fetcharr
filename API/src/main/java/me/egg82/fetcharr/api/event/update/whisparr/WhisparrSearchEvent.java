package me.egg82.fetcharr.api.event.update.whisparr;

import me.egg82.arr.whisparr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fired before submitting a list of {@link MovieResource}s to the *arr app for searching
 */
public class WhisparrSearchEvent extends AbstractCancellableUpdaterEvent {
    private List<MovieResource> resources;

    public WhisparrSearchEvent(@NotNull List<@NotNull MovieResource> resources, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = new ArrayList<>(resources);
    }

    public @NotNull List<@NotNull MovieResource> resources() {
        return resources;
    }

    public void resources(@NotNull List<@NotNull MovieResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WhisparrSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "WhisparrSearchEvent{" +
                "resources=" + resources +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
