package me.egg82.fetcharr.api.event;

import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.type.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCancellableEvent extends AbstractEvent implements Cancellable {
    private final AtomicBoolean cancelState = new AtomicBoolean(false);

    public AbstractCancellableEvent(@NotNull FetcharrAPI api) {
        super(api);
    }

    @Override
    public @NotNull AtomicBoolean cancellationState() {
        return cancelState;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractCancellableEvent that)) return false;
        return Objects.equals(cancelState, that.cancelState);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cancelState);
    }

    @Override
    public String toString() {
        return "AbstractCancellableEvent{" +
                "cancelState=" + cancelState +
                ", api=" + api +
                '}';
    }
}
