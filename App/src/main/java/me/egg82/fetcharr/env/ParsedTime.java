package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedTime {
    private final long time;
    private final TimeUnit unit;

    private static final Pattern timePattern = Pattern.compile("^(\\d+)\\s*(\\w+)$");

    public ParsedTime(@NotNull String time) throws TimeFormatException {
        Matcher timeMatcher = timePattern.matcher(time);
        if (!timeMatcher.matches()) {
            throw new TimeFormatException("Could not match time pattern " + timePattern.pattern() + " to \"" + time + "\"");
        }

        this.time = Long.parseLong(timeMatcher.group(1));

        String unit = timeMatcher.group(2);
        switch (unit) {
            case "n", "ns", "nanos", "nano", "nanoseconds", "nanosecond" -> this.unit = TimeUnit.NANOSECONDS;
            case "micros", "micro", "microseconds", "microsecond" -> this.unit = TimeUnit.MICROSECONDS;
            case "ms", "millis", "milli", "milliseconds", "millisecond" -> this.unit = TimeUnit.MILLISECONDS;
            case "s", "secs", "sec", "seconds", "second" -> this.unit = TimeUnit.SECONDS;
            case "m", "mins", "min", "minutes", "minute" -> this.unit = TimeUnit.MINUTES;
            case "h", "hrs", "hr", "hours", "hour" -> this.unit = TimeUnit.HOURS;
            case "d", "days", "day" -> this.unit = TimeUnit.DAYS;
            default -> throw new TimeFormatException("Could not determine time unit from \"" + time + "\"");
        }
    }

    public ParsedTime(long time, @NotNull TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    public long time() { return time; }

    public @NotNull TimeUnit unit() { return unit; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParsedTime that)) return false;
        return time == that.time && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, unit);
    }

    @Override
    public String toString() {
        return "ParsedTime{" +
                "time=" + time +
                ", unit=" + unit +
                '}';
    }
}
