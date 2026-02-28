package me.egg82.fetcharr.env;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ParsedDateTime {
    public static final ParsedDateTime UNKNOWN = new ParsedDateTime();

    public static @Nullable ParsedDateTime fromString(@NotNull String date) {
        try {
            return new ParsedDateTime(ZonedDateTime.parse(date));
        } catch (DateTimeParseException ignored) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(date));
            } catch (DateTimeParseException ignored2) {
                return null;
            }
        }
    }

    private final LocalDate date;
    private final LocalTime time;

    private ParsedDateTime() {
        this.date = null;
        this.time = null;
    }

    private ParsedDateTime(@Nullable LocalDate date, @Nullable LocalTime time) {
        this.date = date;
        this.time = time;
    }

    private ParsedDateTime(@Nullable LocalDateTime dateTime) {
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
        this.time = dateTime != null ? dateTime.toLocalTime() : null;
    }

    private ParsedDateTime(@Nullable ZonedDateTime dateTime) {
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
        this.time = dateTime != null ? dateTime.toLocalTime() : null;
    }

    public boolean unknown() { return date == null && time == null; }

    public @Nullable LocalDate date() { return this.date; }

    public @Nullable LocalTime time() { return this.time; }

    public @Nullable LocalDateTime dateTime() {
        if (date == null && time == null) {
            return null;
        }
        if (date == null && time != null) {
            return LocalDateTime.of(LocalDate.of(0, 1, 1), time);
        }
        if (date != null && time == null) {
            return LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0));
        }
        return LocalDateTime.of(date, time);
    }

    public @Nullable ZonedDateTime dateTime(@NotNull ZoneId zone) {
        if (date == null && time == null) {
            return null;
        }
        if (date == null && time != null) {
            return ZonedDateTime.of(LocalDate.of(0, 1, 1), time, zone);
        }
        if (date != null && time == null) {
            return ZonedDateTime.of(date, LocalTime.of(0, 0, 0, 0), zone);
        }
        return ZonedDateTime.of(date, time, zone);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParsedDateTime that)) return false;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    @Override
    public String toString() {
        return "ParsedDateTime{" +
                "date=" + date +
                ", time=" + time +
                '}';
    }
}
