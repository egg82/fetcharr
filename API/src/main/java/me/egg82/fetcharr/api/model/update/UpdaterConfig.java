package me.egg82.fetcharr.api.model.update;

import me.egg82.arr.common.ArrType;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;

/**
 * Represents an object containing configuration
 * values for an {@link Updater}.
 */
public interface UpdaterConfig {
    /**
     * Gets the type of the *arr instance associated
     * with this updater.
     *
     * @return the type of the *arr instance
     */
    @NotNull ArrType type();

    /**
     * Gets the URL of the *arr instance associated
     * with this updater.
     *
     * @return the URL for the *arr instance
     */
    @NotNull String url();

    /**
     * Gets the API key of the *arr instance associated
     * with this updater.
     *
     * @return the API key for the *arr instance
     */
    @NotNull String key();

    /**
     * Gets the id of the configuration associated
     * with this updater.
     *
     * @return the id of the updater configuration
     */
    int id();

    /**
     * Gets the search amount configured for
     * this updater.
     *
     * @return the search amount for this updater
     */
    int searchAmount();

    /**
     * Gets the search interval configured for
     * this updater.
     *
     * @return the search interval for this updater
     */
    @NotNull TimeValue searchInterval();

    /**
     * Gets the monitoring value configured for
     * this updater.
     *
     * @return the monitoring value for this updater
     */
    boolean monitoredOnly();

    /**
     * Gets the missing value configured for
     * this updater.
     *
     * @return the missing value for this updater
     */
    boolean missingOnly();

    /**
     * Gets the skip-tags list configured for
     * this updater.
     *
     * @return the skip-tags list for this updater
     */
    @NotNull PSet<@NotNull String> skipTags();

    /**
     * Gets the cutoff value configured for
     * this updater.
     *
     * @return the cutoff value for this updater
     */
    boolean useCutoff();
}
