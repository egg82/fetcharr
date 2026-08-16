package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DurationParser.class);

    private static final Pattern PATTERN = Pattern.compile("^(\\d+):(\\d+)(?::(\\d+))?(?::(\\d+))?$");
    private static final Pattern TIME_SPAN_PATTERN = Pattern.compile("^(-)?(?:(\\d+)\\.)?(\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d{1,7}))?$");

    public static @NotNull Duration get(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull Duration getTimeSpan(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key) {
        return getTimeSpan(def, obj, key, false);
    }

    public static @NotNull Duration get(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        Duration r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull Duration getTimeSpan(@NotNull Duration def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        Duration r = getTimeSpan(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull Duration parseTimeSpan(@NotNull Duration def, @Nullable String val) {
        return parseTimeSpan(def, val, false);
    }

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val, boolean silent) {
        Duration r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @NotNull Duration parseTimeSpan(@NotNull Duration def, @Nullable String val, boolean silent) {
        Duration r = parseTimeSpan(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable Duration get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable Duration getTimeSpan(@Nullable JSONObject obj, @Nullable String key) {
        return getTimeSpan(obj, key, false);
    }

    public static @Nullable Duration get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable Duration getTimeSpan(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parseTimeSpan(StringParser.get(obj, key), silent);
    }

    public static @Nullable Duration parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable Duration parseTimeSpan(@Nullable String val) {
        return parseTimeSpan(val, false);
    }

    public static @Nullable Duration parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        Matcher m = PATTERN.matcher(val);
        if (m.matches()) {
            int last = m.group(4) != null ? 4 : m.group(3) != null ? 3 : 2;
            Duration time = Duration.ofSeconds(NumberParser.parseLong(-1L, m.group(last), silent));
            last--;

            try {
                time = time.plus(Duration.ofMinutes(NumberParser.parseLong(-1L, m.group(last), silent)));
                last--;

                if (last > 0) {
                    time = time.plus(Duration.ofHours(NumberParser.parseLong(-1L, m.group(last), silent)));
                    last--;
                }
                if (last > 0) {
                    time = time.plus(Duration.ofDays(NumberParser.parseLong(-1L, m.group(last), silent)));
                }
            } catch (ArithmeticException ex) {
                if (!silent) {
                    LOGGER.warn("Could not parse duration from string value \"{}\"", val, ex);
                }
                return null;
            }

            return time;
        }

        try {
            return Duration.parse(val);
        } catch (DateTimeParseException ex) {
            int minutes = NumberParser.parseInt(-1, val, true);
            if (minutes >= 0) {
                return Duration.ofMinutes(minutes);
            }

            if (!silent) {
                LOGGER.warn("Could not parse duration from string value \"{}\"", val, ex);
            }
            return null;
        }
    }

    public static @Nullable Duration parseTimeSpan(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        Matcher m = TIME_SPAN_PATTERN.matcher(val);
        if (!m.matches()) {
            if (!silent) {
                LOGGER.warn("Could not parse C# TimeSpan duration from string value \"{}\"", val);
            }
            return null;
        }

        Duration time = Duration.ofNanos(NumberParser.parseLong(0L, rightPad(m.group(6), "0", 9), silent));
        try {
            time = time.plus(Duration.ofSeconds(NumberParser.parseLong(0L, m.group(5), silent)));
            time = time.plus(Duration.ofMinutes(NumberParser.parseLong(0L, m.group(4), silent)));
            time = time.plus(Duration.ofHours(NumberParser.parseLong(0L, m.group(3), silent)));
            time = time.plus(Duration.ofDays(NumberParser.parseLong(0L, m.group(2), silent)));
        } catch (ArithmeticException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse C# TimeSpan duration from string value \"{}\"", val, ex);
            }
            return null;
        }
        return "-".equals(m.group(1)) ? time.negated() : time;
    }

    private static @Nullable String rightPad(@Nullable String text, @NotNull String chars, int len) {
        if (text == null || text.length() >= len) {
            return text;
        }
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < len) {
            builder.append(chars);
        }
        return builder.toString();
    }

    private DurationParser() { }
}
