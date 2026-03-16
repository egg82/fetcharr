package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanParser.class);

    public static boolean get(boolean def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static boolean get(boolean def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return def;
        }
        return parse(def, StringParser.get(obj, key), silent);
    }

    public static boolean parse(boolean def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static boolean parse(boolean def, @Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")) {
            return true;
        } else if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("no")) {
            return false;
        } else {
            int fallback = NumberParser.parseInt(-1, val, true);
            if (fallback > 0) {
                return true;
            } else if (fallback == 0) {
                return false;
            }
        }

        if (!silent) {
            LOGGER.warn("Could not parse boolean from string value \"{}\"", val, new BooleanFormatException(val));
        }
        return def;
    }

    private BooleanParser() { }
}
