package me.egg82.fetcharr.api.event.update.whisparr;

import me.egg82.arr.whisparr.v3.schema.MovieResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired before a {@link MovieResource} is cancelled from being selected for any reason
 *
 * <p>Cancelling this event will make the system continue with this movie even
 * if it would otherwise not be selected for the reason specified.</p>
 */
public class WhisparrSkipMovieSelectionEvent extends AbstractCancellableUpdaterEvent {
    private final MovieResource resource;
    private final SelectionCancellationReason reason;

    public WhisparrSkipMovieSelectionEvent(@NotNull MovieResource resource, @NotNull SelectionCancellationReason reason, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
        this.reason = reason;
    }

    public @NotNull MovieResource resource() {
        return resource;
    }

    public @NotNull SelectionCancellationReason reason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WhisparrSkipMovieSelectionEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource, reason);
    }

    @Override
    public String toString() {
        return "WhisparrSkipMovieSelectionEvent{" +
                "resource=" + resource +
                ", reason=" + reason +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
