package me.egg82.fetcharr.api.event;

import me.egg82.fetcharr.api.FetcharrAPI;
import org.jetbrains.annotations.NotNull;

/**
 * A superinterface for all Fetcharr events.
 */
public interface FetcharrEvent {
    /**
     * Gets the API instance this event was dispatched from.
     *
     * @return the api instance
     */
    @NotNull FetcharrAPI api();

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    @NotNull Class<? extends FetcharrEvent> eventType();
}
