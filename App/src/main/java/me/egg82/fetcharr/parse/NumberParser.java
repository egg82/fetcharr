package me.egg82.fetcharr.parse;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberParser.class);

    public static int parseInt(int def, @Nullable String val) {
        if (val == null) {
            return def;
        }

        val = val.trim();
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Could not parse integer from string value \"{}\"", val, ex);
        }
        return def;
    }

    public static long parseLong(long def, @Nullable String val) {
        if (val == null) {
            return def;
        }

        val = val.trim();
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Could not parse integer from string value \"{}\"", val, ex);
        }
        return def;
    }

    public static float parseFloat(float def, @Nullable String val) {
        if (val == null) {
            return def;
        }

        val = val.trim();
        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Could not parse integer from string value \"{}\"", val, ex);
        }
        return def;
    }

    public static double parseDouble(double def, @Nullable String val) {
        if (val == null) {
            return def;
        }

        val = val.trim();
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Could not parse integer from string value \"{}\"", val, ex);
        }
        return def;
    }

    private NumberParser() { }
}
