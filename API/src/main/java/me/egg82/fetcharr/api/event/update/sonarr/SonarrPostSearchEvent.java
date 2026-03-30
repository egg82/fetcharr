package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.CommandResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after submitting a list of {@link SeriesResource}s to the *arr app for searching
 */
public class SonarrPostSearchEvent extends AbstractUpdaterEvent {
    private final CommandResource resource;

    public SonarrPostSearchEvent(@NotNull CommandResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull CommandResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrPostSearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "SonarrPostSearchEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
