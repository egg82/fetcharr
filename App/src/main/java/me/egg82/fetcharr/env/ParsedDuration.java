package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedDuration {
    public static final ParsedDuration UNKNOWN = new ParsedDuration();

    private final Duration duration;

    private static final Pattern durationPattern = Pattern.compile("^(\\d+):(\\d+):(\\d)(:(\\d))?$");

    private ParsedDuration() throws DurationFormatException {
        this.duration = null;
    }

    public ParsedDuration(@NotNull String duration) throws DurationFormatException {
        Matcher durationMatcher = durationPattern.matcher(duration);
        if (!durationMatcher.matches()) {
            throw new DurationFormatException("Could not match duration pattern " + durationPattern.pattern() + " to \"" + duration + "\"");
        }

        long hours = Long.parseLong(durationMatcher.group(1));
        long minutes = Long.parseLong(durationMatcher.group(2));
        long seconds = Long.parseLong(durationMatcher.group(3));
        long millis = 0;
        if (durationMatcher.groupCount() >= 6) {
            millis = Long.parseLong(durationMatcher.group(5));
        }

        this.duration = Duration.ofMillis(millis + (seconds * 1000L) + (minutes * 60L * 1000L) + (hours * 60L * 60L * 1000L));
    }

    public ParsedDuration(@NotNull Duration duration) {
        this.duration = duration;
    }

    public boolean unknown() { return this.duration == null; }

    public @Nullable Duration duration() { return duration; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParsedDuration that)) return false;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(duration);
    }

    @Override
    public String toString() {
        return "ParsedDuration{" +
                "duration=" + duration +
                '}';
    }
}
