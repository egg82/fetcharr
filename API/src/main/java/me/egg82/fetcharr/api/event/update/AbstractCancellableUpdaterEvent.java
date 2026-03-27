package me.egg82.fetcharr.api.event.update;

import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.AbstractCancellableEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractCancellableUpdaterEvent extends AbstractCancellableEvent {
    protected final Updater updater;

    public AbstractCancellableUpdaterEvent(@NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(api);

        this.updater = updater;
    }

    /**
     * Gets the {@link Updater} for this event.
     *
     * @return the updater for this event
     */
    public @NotNull Updater updater() {
        return updater;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractCancellableUpdaterEvent that)) return false;
        return Objects.equals(updater, that.updater);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(updater);
    }

    @Override
    public String toString() {
        return "AbstractCancellableUpdaterEvent{" +
                "updater=" + updater +
                ", api=" + api +
                '}';
    }
}
