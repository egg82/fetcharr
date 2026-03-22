package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired before a {@link SeriesResource} is cancelled from being selected for any reason
 *
 * <p>Cancelling this event will make the system continue with this movie even
 * if it would otherwise not be selected for the reason specified.</p>
 */
public class SonarrSkipSeriesSelectionEvent extends AbstractCancellableUpdaterEvent {
    private final SeriesResource resource;
    private final SelectionCancellationReason reason;

    public SonarrSkipSeriesSelectionEvent(@NotNull SeriesResource resource, @NotNull SelectionCancellationReason reason, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
        this.reason = reason;
    }

    public @NotNull SeriesResource resource() {
        return resource;
    }

    public @NotNull SelectionCancellationReason reason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrSkipSeriesSelectionEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource, reason);
    }

    @Override
    public String toString() {
        return "SonarrSkipSeriesSelectionEvent{" +
                "resource=" + resource +
                ", reason=" + reason +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
