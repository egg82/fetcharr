package me.egg82.fetcharr.api.event.update;

public enum SelectionCancellationReason {
    UNKNOWN,
    PLUGIN,
    UNMONITORED,
    MISSING,
    NOT_MISSING,
    QUALITY_CUTOFF_MET,
    SKIP_TAG_FOUND
}
