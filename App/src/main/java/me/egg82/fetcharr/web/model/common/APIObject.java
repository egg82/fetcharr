package me.egg82.fetcharr.web.model.common;

import org.jetbrains.annotations.NotNull;

public interface APIObject<T extends APIObject<T>> {
    T fetch(@NotNull String apiKey);

    boolean valid();
    boolean unknown();
    void invalidate();
}
