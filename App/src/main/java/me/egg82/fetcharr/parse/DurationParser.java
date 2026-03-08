package me.egg82.fetcharr.parse;

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

    public static @NotNull Duration parse(@NotNull Duration def, @Nullable String val) {
        Duration r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable Duration parse(@Nullable String val) {
        if (val == null) {
            return null;
        }

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
            LOGGER.warn("Could not parse duration from string value \"{}\"", val, ex);
            return null;
        }
    }

    private DurationParser() { }
}
