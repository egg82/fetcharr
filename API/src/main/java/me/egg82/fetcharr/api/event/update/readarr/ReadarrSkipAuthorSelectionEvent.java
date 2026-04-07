package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired before a {@link AuthorResource} is cancelled from being selected for any reason
 *
 * <p>Cancelling this event will make the system continue with this movie even
 * if it would otherwise not be selected for the reason specified.</p>
 */
public class ReadarrSkipAuthorSelectionEvent extends AbstractCancellableUpdaterEvent {
    private final AuthorResource resource;
    private final SelectionCancellationReason reason;

    public ReadarrSkipAuthorSelectionEvent(@NotNull AuthorResource resource, @NotNull SelectionCancellationReason reason, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
        this.reason = reason;
    }

    public @NotNull AuthorResource resource() {
        return resource;
    }

    public @NotNull SelectionCancellationReason reason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrSkipAuthorSelectionEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource, reason);
    }

    @Override
    public String toString() {
        return "ReadarrSkipAuthorSelectionEvent{" +
                "resource=" + resource +
                ", reason=" + reason +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
