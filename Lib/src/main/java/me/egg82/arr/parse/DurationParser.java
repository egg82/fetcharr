package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.log.FileLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
    private static final Logger LOGGER = new FileLogger(LoggerFactory.getLogger(DurationParser.class));

    private static final Pattern PATTERN = Pattern.compile("^(\\d+):(\\d+)(?::(\\d+))?(?::(\\d+))?$");

    public static @NotNull Duration get(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull Duration get(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        Duration r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val, boolean silent) {
        Duration r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable Duration get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable Duration get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable Duration parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable Duration parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        Matcher m = PATTERN.matcher(val);
        if (m.matches()) {
            Duration time = Duration.ofMinutes(NumberParser.parseLong(-1L, m.group(1)));
            time = time.plus(Duration.ofSeconds(NumberParser.parseLong(-1L, m.group(2))));
            if (m.groupCount() > 2) {
                time = time.plus(Duration.ofHours(NumberParser.parseLong(-1L, m.group(3))));
            }
            if (m.groupCount() > 3) {
                time = time.plus(Duration.ofDays(NumberParser.parseLong(-1L, m.group(4))));
            }
            return time;
        }

        try {
            return Duration.parse(val);
        } catch (DateTimeParseException ex) {
            int minutes = NumberParser.parseInt(-1, val);
            if (minutes >= 0) {
                return Duration.ofMinutes(minutes);
            }

            if (!silent) {
                LOGGER.warn("Could not parse duration from string value \"{}\"", val, ex);
            }
            return null;
        }
    }

    private DurationParser() { }
}
