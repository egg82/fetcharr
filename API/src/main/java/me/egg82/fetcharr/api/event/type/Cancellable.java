package me.egg82.fetcharr.api.event.type;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents an event that can be cancelled
 */
public interface Cancellable extends com.sasorio.event.Cancellable {
    /**
     * Gets an {@link AtomicBoolean} holding the cancellation state of the event
     *
     * @return the cancellation state
     */
    @NotNull AtomicBoolean cancellationState();

    /**
     * Returns true if the event is currently cancelled.
     *
     * @return if the event is cancelled
     */
    default boolean isCancelled() {
        return cancellationState().get();
    }

    /**
     * Returns true if the event is currently cancelled.
     *
     * @return if the event is cancelled
     */
    @Override
    default boolean cancelled() {
        return cancellationState().get();
    }

    /**
     * Returns true if the event is not currently cancelled.
     *
     * @return if the event is not cancelled
     */
    default boolean isNotCancelled() {
        return !cancellationState().get();
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled the new state
     */
    @Override
    default void cancelled(final boolean cancelled) {
        cancellationState().set(cancelled);
    }
}
