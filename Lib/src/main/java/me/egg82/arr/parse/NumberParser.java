package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberParser.class);

    public static int getInt(int def, @Nullable JSONObject obj, @Nullable String key) {
        return getInt(def, obj, key, false);
    }

    public static int getInt(int def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return def;
        }
        return parseInt(def, StringParser.get(obj, key), silent);
    }

    public static int parseInt(int def, @Nullable String val) {
        return parseInt(def, val, false);
    }

    public static int parseInt(int def, @Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse int from string value \"{}\"", val, ex);
            }
        }
        return def;
    }

    public static long getLong(long def, @Nullable JSONObject obj, @Nullable String key) {
        return getLong(def, obj, key, false);
    }

    public static long getLong(long def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return def;
        }
        return parseLong(def, StringParser.get(obj, key), silent);
    }

    public static long parseLong(long def, @Nullable String val) {
        return parseLong(def, val, false);
    }

    public static long parseLong(long def, @Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse long from string value \"{}\"", val, ex);
            }
        }
        return def;
    }

    public static float getFloat(float def, @Nullable JSONObject obj, @Nullable String key) {
        return getFloat(def, obj, key, false);
    }

    public static float getFloat(float def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return def;
        }
        return parseFloat(def, StringParser.get(obj, key), silent);
    }

    public static float parseFloat(float def, @Nullable String val) {
        return parseFloat(def, val, false);
    }

    public static float parseFloat(float def, @Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse float from string value \"{}\"", val, ex);
            }
        }
        return def;
    }

    public static double getDouble(double def, @Nullable JSONObject obj, @Nullable String key) {
        return getDouble(def, obj, key, false);
    }

    public static double getDouble(double def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return def;
        }
        return parseDouble(def, StringParser.get(obj, key), silent);
    }

    public static double parseDouble(double def, @Nullable String val) {
        return parseDouble(def, val, false);
    }

    public static double parseDouble(double def, @Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse double from string value \"{}\"", val, ex);
            }
        }
        return def;
    }
}
