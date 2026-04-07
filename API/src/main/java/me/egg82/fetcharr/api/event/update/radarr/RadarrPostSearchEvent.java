package me.egg82.fetcharr.api.event.update.radarr;

import me.egg82.arr.radarr.v3.schema.CommandResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Collection;
import java.util.Objects;

/**
 * Fired after submitting a list of {@link MovieResource}s to the *arr app for searching
 */
public class RadarrPostSearchEvent extends AbstractUpdaterEvent {
    private final PVector<@NotNull MovieResource> resources;
    private final CommandResource result;

    public RadarrPostSearchEvent(@NotNull Collection<@NotNull MovieResource> resources, @NotNull CommandResource result, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resources = TreePVector.from(resources);
        this.result = result;
    }

    public @NotNull PVector<@NotNull MovieResource> resources() {
        return resources;
    }

    public @NotNull CommandResource result() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resources, that.resources) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources, result);
    }

    @Override
    public String toString() {
        return "RadarrPostSearchEvent{" +
                "resources=" + resources +
                ", result=" + result +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
