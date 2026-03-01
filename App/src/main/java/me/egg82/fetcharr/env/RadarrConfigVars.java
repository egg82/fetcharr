package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum RadarrConfigVars {
    RADARR_URL(String.class, "RADARR_{}_URL", null),
    RADARR_API_KEY(String.class, "RADARR_{}_API_KEY", null),
    RADARR_SEARCH_AMOUNT(Integer.class, "RADARR_{}_SEARCH_AMOUNT", ConfigVars.SEARCH_AMOUNT.def()),
    RADARR_SEARCH_INTERVAL(ParsedTime.class, "RADARR_{}_SEARCH_INTERVAL", ConfigVars.SEARCH_INTERVAL.def()),
    RADARR_MONITORED_ONLY(Boolean.class, "RADARR_{}_MONITORED_ONLY", ConfigVars.MONITORED_ONLY.def()),
    RADARR_SKIP_TAGS(String.class, "RADARR_{}_SKIP_TAGS", ConfigVars.SKIP_TAGS.def());

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigVars.class);

    private final Class<?> type;
    private final String name;
    private final Object def;

    <T> RadarrConfigVars(@NotNull Class<T> type, @NotNull String name, T def) {
        this.type = type;
        this.name = name;
        this.def = def;
    }

    public @NotNull String name(int num) {
        return this.name.replace("{}", String.valueOf(num));
    }

    public @NotNull Class<?> type() { return this.type; }

    public <T> T def() { return (T) this.def; }

    public static boolean hasVar(@NotNull RadarrConfigVars var, int num) {
        return System.getenv(var.name(num)) != null;
    }

    public static @Nullable String getVar(@NotNull RadarrConfigVars var, int num) {
        return System.getenv(var.name(num));
    }

    public static @NotNull String getVar(@NotNull RadarrConfigVars var, int num, @NotNull String def) {
        String val = System.getenv(var.name(num));
        return val != null ? val : def;
    }

    public static int getVar(@NotNull RadarrConfigVars var, int num, int def) {
        return toInt(var, num, def);
    }

    private static int toInt(@NotNull RadarrConfigVars var, int num, int def) {
        String v = System.getenv(var.name(num));
        if (v == null) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ignored) {
            LOGGER.warn("Could not transform environment variable {} to integer: {}", var.name(num), System.getenv(var.name(num)));
        }
        return def;
    }

    private static int toIntInternal(@NotNull String var, int def) {
        try {
            return Integer.parseInt(var);
        } catch (NumberFormatException ignored) { }
        return def;
    }

    public static boolean getVar(@NotNull RadarrConfigVars var, int num, boolean def) {
        return toBool(var, num, def);
    }

    private static boolean toBool(@NotNull RadarrConfigVars var, int num, boolean def) {
        String v = System.getenv(var.name(num));
        if (v == null) {
            return def;
        }

        v = v.strip();
        int i = toIntInternal(v, -1);

        if (
                v.equalsIgnoreCase("true")
                || v.equalsIgnoreCase("yes")
                || i > 0
        ) {
            return true;
        }

        if (
                v.equalsIgnoreCase("false")
                || v.equalsIgnoreCase("no")
                || i == 0
        ) {
            return false;
        }

        LOGGER.warn("Could not transform environment variable {} to boolean: {}", var.name(num), System.getenv(var.name(num)));
        return def;
    }

    public static @NotNull ParsedTime getVar(@NotNull RadarrConfigVars var, int num, @NotNull ParsedTime def) {
        return toParsedTime(var, num, def);
    }

    private static @NotNull ParsedTime toParsedTime(@NotNull RadarrConfigVars var, int num, @NotNull ParsedTime def) {
        String v = System.getenv(var.name(num));
        if (v == null) {
            return def;
        }
        try {
            return new ParsedTime(v);
        } catch (TimeFormatException ignored) {
            LOGGER.warn("Could not transform environment variable {} to time: {}", var.name(num), System.getenv(var.name(num)));
        }
        return def;
    }
}
