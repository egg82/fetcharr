package me.egg82.fetcharr.parse;

import me.egg82.fetcharr.ex.TimeValueFormatException;
import me.egg82.fetcharr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeValueParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeValueParser.class);

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)\\s*(\\w+)$");

    public static @NotNull TimeValue parse(@NotNull TimeValue def, @Nullable String val) {
        TimeValue r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable TimeValue parse(@Nullable String val) {
        if (val == null) {
            return null;
        }

        Matcher m = PATTERN.matcher(val);
        if (!m.matches()) {
            LOGGER.warn("Could not parse TimeValue from string value \"{}\"", val, new TimeValueFormatException(val));
            return null;
        }

        long time = NumberParser.parseLong(-1L, m.group(1));
        if (time < 0) {
            LOGGER.warn("Could not parse TimeValue time from string value \"{}\"", val, new TimeValueFormatException(val));
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
            LOGGER.warn("Could not parse TimeValue unit from string value \"{}\"", val, new TimeValueFormatException(val));
            return null;
        }

        return new TimeValue(time, unit);
    }

    private TimeValueParser() { }
}
