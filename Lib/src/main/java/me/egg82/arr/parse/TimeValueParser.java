package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.log.FileLogger;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeValueParser {
    private static final Logger LOGGER = new FileLogger(LoggerFactory.getLogger(TimeValueParser.class));

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)\\s*(\\w+)$");

    public static @NotNull TimeValue get(@NotNull TimeValue def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull TimeValue get(@NotNull TimeValue def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        TimeValue r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull TimeValue parse(@NotNull TimeValue def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull TimeValue parse(@NotNull TimeValue def, @Nullable String val, boolean silent) {
        TimeValue r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable TimeValue get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable TimeValue get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable TimeValue parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable TimeValue parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        Matcher m = PATTERN.matcher(val);
        if (!m.matches()) {
            if (!silent) {
                LOGGER.warn("Could not parse TimeValue from string value \"{}\"", val, new TimeValueFormatException(val));
            }
            return null;
        }

        long time = NumberParser.parseLong(-1L, m.group(1));
        if (time < 0) {
            if (!silent) {
                LOGGER.warn("Could not parse TimeValue time from string value \"{}\"", val, new TimeValueFormatException(val));
            }
            return null;
        }

        TimeUnit unit = switch (m.group(2)) {
            case "n", "ns", "nanos", "nano", "nanoseconds", "nanosecond" -> TimeUnit.NANOSECONDS;
            case "micros", "micro", "microseconds", "microsecond" -> TimeUnit.MICROSECONDS;
            case "ms", "millis", "milli", "milliseconds", "millisecond" -> TimeUnit.MILLISECONDS;
            case "s", "secs", "sec", "seconds", "second" -> TimeUnit.SECONDS;
            case "m", "mins", "min", "minutes", "minute" -> TimeUnit.MINUTES;
            case "h", "hrs", "hr", "hours", "hour" -> TimeUnit.HOURS;
            case "d", "days", "day" -> TimeUnit.DAYS;
            default -> null;
        };
        if (unit == null) {
            if (!silent) {
                LOGGER.warn("Could not parse TimeValue unit from string value \"{}\"", val, new TimeValueFormatException(val));
            }
            return null;
        }

        return new TimeValue(time, unit);
    }

    private TimeValueParser() { }
}
