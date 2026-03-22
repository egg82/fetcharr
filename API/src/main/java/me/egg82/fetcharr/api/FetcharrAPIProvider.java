package me.egg82.fetcharr.api;

import org.jetbrains.annotations.NotNull;

/**
 * Provides static access to the {@link FetcharrAPI} instance
 */
public class FetcharrAPIProvider {
    private static FetcharrAPI instance = null;

    /**
     * Return the current instance of the running {@link FetcharrAPI} service.
     *
     * @return The current running instance of the {@link FetcharrAPI} service
     *
     * @throws IllegalStateException if not yet loaded, or unloaded
     */
    public static @NotNull FetcharrAPI instance() {
        FetcharrAPI r = instance;
        if (r == null) {
            throw new IllegalStateException("FetcharrAPI is not loaded.");
        }
        return r;
    }

    private static void register(@NotNull FetcharrAPI instance) {
        FetcharrAPIProvider.instance = instance;
    }

    private static void deregister() {
        FetcharrAPIProvider.instance = null;
    }

    private FetcharrAPIProvider() { }
}
