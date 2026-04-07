package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.arr.readarr.v1.schema.CommandResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Collection;
import java.util.Objects;

/**
 * Fired after submitting a list of {@link AuthorResource}s to the *arr app for searching
 */
public class ReadarrPostSearchEvent extends AbstractUpdaterEvent {
    private final PVector<@NotNull AuthorResource> resources;
    private final PVector<@NotNull CommandResource> results;

    public ReadarrPostSearchEvent(@NotNull Collection<@NotNull AuthorResource> resources, @NotNull Collection<@NotNull CommandResource> results, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = TreePVector.from(resources);
        this.results = TreePVector.from(results);
    }

    public @NotNull PVector<@NotNull AuthorResource> resources() {
        return resources;
    }

    public @NotNull PVector<@NotNull CommandResource> results() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources) && Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources, results);
    }

    @Override
    public String toString() {
        return "ReadarrPostSearchEvent{" +
                "resources=" + resources +
                ", results=" + results +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
