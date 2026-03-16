package me.egg82.arr.unit;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeValue {
    private final long time;
    private final TimeUnit unit;
    private final Duration duration;

    public TimeValue(long time, @NotNull TimeUnit unit) {
        this.time = time;
        this.unit = unit;
        this.duration = Duration.ofMillis(unit.toMillis(time));
    }

    public TimeValue(@NotNull Duration duration) {
        this.time = duration.toMillis();
        this.unit = TimeUnit.MILLISECONDS;
        this.duration = duration;
    }

    public long time() {
        return time;
    }

    public @NotNull TimeUnit unit() {
        return unit;
    }

    public @NotNull Duration duration() {
        return this.duration;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeValue timeValue)) return false;
        return time == timeValue.time && unit == timeValue.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, unit);
    }

    @Override
    public String toString() {
        return "TimeValue{" +
                "time=" + time +
                ", unit=" + unit +
                ", duration=" + duration +
                '}';
    }
}
