package me.egg82.fetcharr.api.model.update;

import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;

/**
 * Represents the object responsible for managing {@link Updater} instances.
 */
public interface UpdateManager {
    /**
     * Gets the current list of {@link Updater}s
     *
     * @return the current list of updaters
     */
    @NotNull PVector<@NotNull Updater> updaters();

    /**
     * Attempts to register a new {@link Updater}
     *
     * @param updater the updater to register
     * @return true if successful, false if not
     */
    boolean register(@NotNull Updater updater);

    /**
     * Attempts to unregister an existing {@link Updater}
     *
     * @param updater the updater to unregister
     * @return true if successful, false if not
     */
    boolean unregister(@NotNull Updater updater);

    /**
     * Shut each {@link Updater} down, giving them
     * a certain amount of time to gracefully
     * terminate.
     *
     * @param waitMillis the amount of time to wait for shutdown
     */
    void shutdown(long waitMillis);

    /**
     * True if Fetcharr is in dry-run mode, false if not.
     *
     * @return true if in dry-run mode, false if not
     */
    boolean dryRun();
}
