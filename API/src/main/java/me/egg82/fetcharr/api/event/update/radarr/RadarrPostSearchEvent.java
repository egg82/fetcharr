package me.egg82.fetcharr.api.event.update.radarr;

import me.egg82.arr.radarr.v3.schema.CommandResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after submitting a list of {@link MovieResource}s to the *arr app for searching
 */
public class RadarrPostSearchEvent extends AbstractUpdaterEvent {
    private final CommandResource resource;

    public RadarrPostSearchEvent(@NotNull CommandResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull CommandResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "RadarrPostSearchEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
