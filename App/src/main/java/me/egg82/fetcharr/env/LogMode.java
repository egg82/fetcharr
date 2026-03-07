package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LogMode {
    TRACE(new String[] { "trace", "tracing" }),
    DEBUG(new String[] { "debug", "debugging", "debugger" }),
    INFO(new String[] { "info", "information", "informational" }),
    WARN(new String[] { "warn", "warning" }),
    ERROR(new String[] { "err", "error", "crit", "critical" });

    private final String[] names;
    LogMode(@NotNull String @NotNull [] names) {
        this.names = names;
    }

    public @NotNull String @NotNull [] names() {
        return names;
    }

    public static @NotNull LogMode parse(@NotNull LogMode def, @Nullable String val) {
        LogMode r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable LogMode parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (LogMode m : LogMode.values()) {
            for (String v : m.names) {
                if (v.equalsIgnoreCase(val)) {
                    return m;
                }
            }
        }
        return null;
    }
}
