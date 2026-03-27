package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired before a {@link ArtistResource} is cancelled from being selected for any reason
 *
 * <p>Cancelling this event will make the system continue with this movie even
 * if it would otherwise not be selected for the reason specified.</p>
 */
public class LidarrSkipArtistSelectionEvent extends AbstractCancellableUpdaterEvent {
    private final ArtistResource resource;
    private final SelectionCancellationReason reason;

    public LidarrSkipArtistSelectionEvent(@NotNull ArtistResource resource, @NotNull SelectionCancellationReason reason, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.resource = resource;
        this.reason = reason;
    }

    public @NotNull ArtistResource resource() {
        return resource;
    }

    public @NotNull SelectionCancellationReason reason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrSkipArtistSelectionEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource, reason);
    }

    @Override
    public String toString() {
        return "LidarrSkipArtistSelectionEvent{" +
                "resource=" + resource +
                ", reason=" + reason +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
