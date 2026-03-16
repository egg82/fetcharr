package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.unit.ResolutionValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolutionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionParser.class);

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)x(\\d+)$");

    public static @NotNull ResolutionValue get(@NotNull ResolutionValue def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull ResolutionValue get(@NotNull ResolutionValue def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        ResolutionValue r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull ResolutionValue parse(@NotNull ResolutionValue def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull ResolutionValue parse(@NotNull ResolutionValue def, @Nullable String val, boolean silent) {
        ResolutionValue r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable ResolutionValue get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable ResolutionValue get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable ResolutionValue parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable ResolutionValue parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        Matcher m = PATTERN.matcher(val);
        if (!m.matches()) {
            if (!silent) {
                LOGGER.warn("Could not parse ResolutionValue from string value \"{}\"", val, new ResolutionValueFormatException(val));
            }
            return null;
        }

        int horizontal = NumberParser.parseInt(-1, m.group(1));
        if (horizontal < 0) {
            if (!silent) {
                LOGGER.warn("Could not parse ResolutionValue horizontal from string value \"{}\"", val, new ResolutionValueFormatException(val));
            }
            return null;
        }

        int vertical = NumberParser.parseInt(-1, m.group(2));
        if (vertical < 0) {
            if (!silent) {
                LOGGER.warn("Could not parse ResolutionValue vertical from string value \"{}\"", val, new ResolutionValueFormatException(val));
            }
            return null;
        }

        return new ResolutionValue(horizontal, vertical);
    }

    private ResolutionParser() { }
}
