package me.egg82.fetcharr.api.model.update;

import me.egg82.arr.common.ArrAPI;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Represents an object responsible for running updates
 * for a single *arr instance.
 */
public interface Updater {
    /**
     * Run the updater, providing the {@link Instant}
     * for the previous run.
     *
     * @param previousRun the instant of the previous run
     */
    void run(@NotNull Instant previousRun);

    /**
     * Gets the {@link ArrAPI} associated with
     * this updater instance.
     *
     * @return the *arr API for this instance
     */
    @NotNull ArrAPI arrApi();

    /**
     * Gets the configuration for this updater instance.
     *
     * @return the configuration for this updater
     */
    @NotNull UpdaterConfig config();
}
