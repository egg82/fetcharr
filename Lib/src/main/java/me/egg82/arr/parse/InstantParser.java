package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.log.FileLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public class InstantParser {
    private static final Logger LOGGER = new FileLogger(LoggerFactory.getLogger(InstantParser.class));

    public static @NotNull Instant get(@NotNull Instant def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull Instant get(@NotNull Instant def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        Instant r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull Instant parse(@NotNull Instant def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull Instant parse(@NotNull Instant def, @Nullable String val, boolean silent) {
        Instant r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable Instant get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable Instant get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable Instant parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable Instant parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        try {
            return Instant.parse(val);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(val).atStartOfDay(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException ex) {
                if (!silent) {
                    LOGGER.warn("Could not parse instant from string value \"{}\"", val, ex);
                }
                return null;
            }
        }
    }

    private InstantParser() { }
}
