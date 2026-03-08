package me.egg82.fetcharr.parse;

import me.egg82.fetcharr.ex.ResolutionValueFormatException;
import me.egg82.fetcharr.unit.ResolutionValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolutionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionParser.class);

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)x(\\d+)$");

    public static @NotNull ResolutionValue parse(@NotNull ResolutionValue def, @Nullable String val) {
        ResolutionValue r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable ResolutionValue parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }

        Matcher m = PATTERN.matcher(val);
        if (!m.matches()) {
            LOGGER.warn("Could not parse ResolutionValue from string value \"{}\"", val, new ResolutionValueFormatException(val));
            return null;
        }

        int horizontal = NumberParser.parseInt(-1, m.group(1));
        if (horizontal < 0) {
            LOGGER.warn("Could not parse ResolutionValue horizontal from string value \"{}\"", val, new ResolutionValueFormatException(val));
            return null;
        }

        int vertical = NumberParser.parseInt(-1, m.group(2));
        if (vertical < 0) {
            LOGGER.warn("Could not parse ResolutionValue vertical from string value \"{}\"", val, new ResolutionValueFormatException(val));
            return null;
        }

        return new ResolutionValue(horizontal, vertical);
    }

    private ResolutionParser() { }
}
