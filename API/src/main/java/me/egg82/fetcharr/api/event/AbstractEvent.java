package me.egg82.fetcharr.api.event;

import me.egg82.fetcharr.api.FetcharrAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractEvent implements FetcharrEvent {
    protected final FetcharrAPI api;
    private final Class<? extends FetcharrEvent> clazz;

    public AbstractEvent(@NotNull FetcharrAPI api) {
        this.api = api;
        this.clazz = getClass();
    }

    public @NotNull FetcharrAPI api() {
        return api;
    }

    public @NotNull Class<? extends FetcharrEvent> eventType() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractEvent that)) return false;
        return Objects.equals(api, that.api);
    }

    @Override
    public int hashCode() {
        return Objects.hash(api);
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "api=" + api +
                ", clazz=" + clazz +
                '}';
    }
}
