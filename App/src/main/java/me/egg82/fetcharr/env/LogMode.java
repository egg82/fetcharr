package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LogMode {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;

    public static @NotNull LogMode getMode(@Nullable String mode, @NotNull LogMode def) {
        if (mode == null) {
            return def;
        }

        mode = mode.strip();

        if (
                mode.equalsIgnoreCase("trace")
                || mode.equalsIgnoreCase("tracing")
        ) {
            return LogMode.TRACE;
        }

        if (
                mode.equalsIgnoreCase("debug")
                || mode.equalsIgnoreCase("debugging")
                || mode.equalsIgnoreCase("debugger")
        ) {
            return LogMode.DEBUG;
        }

        if (
                mode.equalsIgnoreCase("info")
                || mode.equalsIgnoreCase("information")
                || mode.equalsIgnoreCase("informational")
        ) {
            return LogMode.INFO;
        }

        if (
                mode.equalsIgnoreCase("warn")
                || mode.equalsIgnoreCase("warning")
        ) {
            return LogMode.WARN;
        }

        if (
                mode.equalsIgnoreCase("err")
                || mode.equalsIgnoreCase("error")
                || mode.equalsIgnoreCase("crit")
                || mode.equalsIgnoreCase("critical")
        ) {
            return LogMode.ERROR;
        }

        return def;
    }
}
