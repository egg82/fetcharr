package me.egg82.fetcharr.api.event.update;

import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after an updater is registered.
 */
public class UpdaterPostRegistrationEvent extends AbstractUpdaterEvent {
    public UpdaterPostRegistrationEvent(@NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);
    }

    @Override
    public String toString() {
        return "UpdaterPostRegistrationEvent{" +
                "updater=" + updater +
                ", api=" + api +
                '}';
    }
}
