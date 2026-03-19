package me.egg82.arr.config;

import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Tristate {
    AUTO(new String[] { "auto", "automatic", "automated", "automation" }),
    TRUE(new String[]{}),
    FALSE(new String[]{});

    private final String[] names;
    Tristate(@NotNull String @NotNull [] names) {
        this.names = names;
    }

    public @NotNull String @NotNull [] names() {
        return names;
    }

    public static @NotNull Tristate parse(@NotNull Tristate def, @Nullable String val) {
        if (val == null || val.isBlank()) {
            return def;
        }
        val = val.trim();

        for (Tristate m : Tristate.values()) {
            for (String v : m.names) {
                if (v.equalsIgnoreCase(val)) {
                    return m;
                }
            }
        }

        // Bit of a hack, but it's fine since this whole app isn't exactly performance-critical
        // Basically if we get the default value in both cases when parsing then it means parsing failed
        // So, we return the default value in that case
        // Otherwise, parsing succeeded and we need to return whatever value parsing succeeded with
        // It doesn't matter which value we use when returning the parsed reult at that point

        // We want to use the BooleanParser because if boolean parsing changes at some point for
        //   whatever reason then we don't want to have to remember to change it in multiple places
        // This idea is more important than a few CPU cycles saved by trying some magic or duplicated code

        boolean defFalse = BooleanParser.parse(false, val, true);
        boolean defTrue = BooleanParser.parse(true, val, true);
        if (!defFalse && defTrue) {
            return def;
        }

        return defTrue ? TRUE : FALSE;
    }
}
