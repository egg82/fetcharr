package me.egg82.fetcharr.parse;

import me.egg82.fetcharr.ex.BooleanFormatException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanParser.class);

    public static boolean parse(boolean def, @Nullable String val) {
        if (val == null || val.isBlank()) {
            return def;
        }

        val = val.trim();
        if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")) {
            return true;
        } else if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("no")) {
            return false;
        } else {
            int fallback = NumberParser.parseInt(-1, val);
            if (fallback > 0) {
                return true;
            } else if (fallback == 0) {
                return false;
            }
        }

        LOGGER.warn("Could not parse boolean from string value \"{}\"", val, new BooleanFormatException(val));
        return def;
    }

    private BooleanParser() { }
}
