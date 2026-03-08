package me.egg82.fetcharr.parse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public class InstantParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstantParser.class);

    public static @NotNull Instant parse(@NotNull Instant def, @Nullable String val) {
        Instant r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable Instant parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(val);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(val).atStartOfDay(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException ex) {
                LOGGER.warn("Could not parse instant from string value \"{}\"", val, ex);
                return null;
            }
        }
    }

    private InstantParser() { }
}
