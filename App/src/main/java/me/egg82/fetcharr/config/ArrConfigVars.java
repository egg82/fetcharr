package me.egg82.fetcharr.config;

import me.egg82.arr.common.ArrType;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.TimeValueParser;
import me.egg82.arr.unit.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArrConfigVars {
    URL(String.class, "{ARR}_{ID}_URL", null),
    API_KEY(String.class, "{ARR}_{ID}_API_KEY", null),
    SEARCH_AMOUNT(Integer.class, "{ARR}_{ID}_SEARCH_AMOUNT", CommonConfigVars.getInt(CommonConfigVars.SEARCH_AMOUNT)),
    SEARCH_INTERVAL(TimeValue.class, "{ARR}_{ID}_SEARCH_INTERVAL", CommonConfigVars.getTimeValue(CommonConfigVars.SEARCH_INTERVAL)),
    MONITORED_ONLY(Boolean.class, "{ARR}_{ID}_MONITORED_ONLY", CommonConfigVars.getBool(CommonConfigVars.MONITORED_ONLY)),
    @Deprecated
    MISSING_ONLY(Boolean.class, "{ARR}_{ID}_MISSING_ONLY", CommonConfigVars.getBool(CommonConfigVars.MISSING_ONLY)),
    MISSING_STATUS(me.egg82.fetcharr.api.model.update.MissingStatus.class, "{ARR}_{ID}_MISSING_STATUS", CommonConfigVars.getMissingStatus(CommonConfigVars.MISSING_STATUS)),
    SKIP_TAGS(String[].class, "{ARR}_{ID}_SKIP_TAGS", CommonConfigVars.getArr(CommonConfigVars.SKIP_TAGS)),
    USE_CUTOFF(Boolean.class, "{ARR}_{ID}_USE_CUTOFF", CommonConfigVars.getBool(CommonConfigVars.USE_CUTOFF));

    private final Class<?> type;
    private final String envName;
    private final Object def;

    <T> ArrConfigVars(@NotNull Class<T> type, @NotNull String envName, T def) {
        this.type = type;
        this.envName = envName;
        this.def = def;
    }

    public @NotNull Class<?> type() {
        return this.type;
    }

    public @NotNull String envName(@NotNull ArrType type, int id) {
        return this.envName.replace("{ARR}", type.name()).replace("{ID}", String.valueOf(id));
    }

    public <T> T def() {
        return (T) this.def;
    }

    public static boolean has(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        return System.getenv(var.envName(type, id)) != null;
    }

    public static @Nullable String get(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        String r = System.getenv(var.envName(type, id));
        return r != null ? r : var.def();
    }

    public static @NotNull String @NotNull [] getArr(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        String r = System.getenv(var.envName(type, id));
        return r != null ? r.split(",") : var.def();
    }

    public static boolean getBool(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        return BooleanParser.parse(var.def(), System.getenv(var.envName(type, id)));
    }

    public static int getInt(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        return NumberParser.parseInt(var.def(), System.getenv(var.envName(type, id)));
    }

    public static @NotNull TimeValue getTimeValue(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        return TimeValueParser.parse(var.def(), System.getenv(var.envName(type, id)));
    }

    public static @NotNull me.egg82.fetcharr.api.model.update.MissingStatus getMissingStatus(@NotNull ArrConfigVars var, @NotNull ArrType type, int id) {
        return me.egg82.fetcharr.api.model.update.MissingStatus.parse(var.def(), System.getenv(var.envName(type, id)));
    }
}
