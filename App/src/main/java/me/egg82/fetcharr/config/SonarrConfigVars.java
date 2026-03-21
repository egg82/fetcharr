package me.egg82.fetcharr.config;

import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.FileParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.TimeValueParser;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public enum SonarrConfigVars {
    URL(String.class, "SONARR_{}_URL", null),
    API_KEY(String.class, "SONARR_{}_API_KEY", null),
    SEARCH_AMOUNT(Integer.class, "SONARR_{}_SEARCH_AMOUNT", CommonConfigVars.getInt(CommonConfigVars.SEARCH_AMOUNT)),
    SEARCH_INTERVAL(TimeValue.class, "SONARR_{}_SEARCH_INTERVAL", CommonConfigVars.getTimeValue(CommonConfigVars.SEARCH_INTERVAL)),
    MONITORED_ONLY(Boolean.class, "SONARR_{}_MONITORED_ONLY", CommonConfigVars.getBool(CommonConfigVars.MONITORED_ONLY)),
    MISSING_ONLY(Boolean.class, "SONARR_{}_MISSING_ONLY", CommonConfigVars.getBool(CommonConfigVars.MISSING_ONLY)),
    SKIP_TAGS(String[].class, "SONARR_{}_SKIP_TAGS", CommonConfigVars.getArr(CommonConfigVars.SKIP_TAGS)),
    USE_CUTOFF(Boolean.class, "SONARR_{}_USE_CUTOFF", CommonConfigVars.getBool(CommonConfigVars.USE_CUTOFF));

    private final Class<?> type;
    private final String envName;
    private final Object def;

    <T> SonarrConfigVars(@NotNull Class<T> type, @NotNull String envName, T def) {
        this.type = type;
        this.envName = envName;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public @NotNull String envName(int id) {
        return this.envName.replace("{}", String.valueOf(id));
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull SonarrConfigVars var, int id) {
        return System.getenv(var.envName(id)) != null;
    }

    public static @Nullable String get(@NotNull SonarrConfigVars var, int id) {
        String r = System.getenv(var.envName(id));
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull SonarrConfigVars var, int id) {
        String r = System.getenv(var.envName(id));
        return r != null ? r.split(",") : var.def();
    }

    public static @NotNull LogMode getLogMode(@NotNull SonarrConfigVars var, int id) {
        return LogMode.parse(var.def(), System.getenv(var.envName(id)));
    }

    public static boolean getBool(@NotNull SonarrConfigVars var, int id) {
        return BooleanParser.parse(var.def(), System.getenv(var.envName(id)));
    }

    public static int getInt(@NotNull SonarrConfigVars var, int id) {
        return NumberParser.parseInt(var.def(), System.getenv(var.envName(id)));
    }

    public static long getLong(@NotNull SonarrConfigVars var, int id) {
        return NumberParser.parseLong(var.def(), System.getenv(var.envName(id)));
    }

    public static float getFloat(@NotNull SonarrConfigVars var, int id) {
        return NumberParser.parseFloat(var.def(), System.getenv(var.envName(id)));
    }

    public static double getDouble(@NotNull SonarrConfigVars var, int id) {
        return NumberParser.parseDouble(var.def(), System.getenv(var.envName(id)));
    }

    public static @NotNull TimeValue getTimeValue(@NotNull SonarrConfigVars var, int id) {
        return TimeValueParser.parse(var.def(), System.getenv(var.envName(id)));
    }

    public static @NotNull File getFile(@NotNull SonarrConfigVars var, int id) {
        return FileParser.parse(var.def(), System.getenv(var.envName(id)));
    }
}
