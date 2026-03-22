package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PluginClassLoaderResolver {
    @Nullable EnabledPluginImpl resolve(@NotNull EnabledPluginImpl requester, @NotNull String className);
}
