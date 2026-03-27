package me.egg82.fetcharr.api.event.update;

import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

/**
 * Fired before an updater is unregistered.
 */
public class UpdaterPreDeregistrationEvent extends AbstractCancellableUpdaterEvent {
    public UpdaterPreDeregistrationEvent(@NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);
    }

    @Override
    public String toString() {
        return "UpdaterPreDeregistrationEvent{" +
                "updater=" + updater +
                ", api=" + api +
                '}';
    }
}
