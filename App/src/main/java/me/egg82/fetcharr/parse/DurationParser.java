package me.egg82.fetcharr.parse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DurationParser.class);

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val) {
        Duration r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable Duration parse(@Nullable String val) {
        if (val == null) {
            return null;
        }

        try {
            return Duration.parse(val);
        } catch (DateTimeParseException ex) {
            LOGGER.warn("Could not parse duration from string value \"{}\"", val, ex);
            return null;
        }
    }

    private DurationParser() { }
}
