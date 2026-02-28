package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;

public class TimeFormatException extends RuntimeException {
    public TimeFormatException(@NotNull String message) {
        super(message);
    }
}
