package me.egg82.fetcharr.util;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface Weighted {
    @NotNull Instant lastUpdated();

    @NotNull Instant lastSelected();
    void lastSelectedNow();
}
