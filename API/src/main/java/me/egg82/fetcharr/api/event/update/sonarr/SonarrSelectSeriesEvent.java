package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired when a {@link SeriesResource} is selected for updating, before any other checks are done
 */
public class SonarrSelectSeriesEvent extends AbstractCancellableUpdaterEvent {
    private final SeriesResource resource;

    public SonarrSelectSeriesEvent(@NotNull SeriesResource resource, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
    }

    public @NotNull SeriesResource resource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrSelectSeriesEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }

    @Override
    public String toString() {
        return "SonarrSelectSeriesEvent{" +
                "resource=" + resource +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
