package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fired before submitting a list of {@link SeriesResource}s to the *arr app for searching
 */
public class SonarrSearchEvent extends AbstractCancellableUpdaterEvent {
    private List<SeriesResource> resources;

    public SonarrSearchEvent(@NotNull List<@NotNull SeriesResource> resources, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = new ArrayList<>(resources);
    }

    public @NotNull List<@NotNull SeriesResource> resources() {
        return resources;
    }

    public void resources(@NotNull List<@NotNull SeriesResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return "SonarrSearchEvent{" +
                "resources=" + resources +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
