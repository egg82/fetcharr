package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.CommandResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Collection;
import java.util.Objects;

/**
 * Fired after submitting a list of {@link SeriesResource}s to the *arr app for searching
 */
public class SonarrPostSearchEvent extends AbstractUpdaterEvent {
    private final PVector<@NotNull SeriesResource> resources;
    private final PVector<@NotNull CommandResource> results;

    public SonarrPostSearchEvent(@NotNull Collection<@NotNull SeriesResource> resources, @NotNull Collection<@NotNull CommandResource> results, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = TreePVector.from(resources);
        this.results = TreePVector.from(results);
    }

    public @NotNull PVector<@NotNull SeriesResource> resources() {
        return resources;
    }

    public @NotNull PVector<@NotNull CommandResource> results() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources) && Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources, results);
    }

    @Override
    public String toString() {
        return "SonarrPostSearchEvent{" +
                "resources=" + resources +
                ", results=" + results +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
