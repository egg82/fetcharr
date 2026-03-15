package me.egg82.arr.parse;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TimeValueFormatException extends RuntimeException {
    private final String val;

    public TimeValueFormatException(@NotNull String val) {
        this.val = val;
    }

    public @NotNull String val() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeValueFormatException that)) return false;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }

    @Override
    public String toString() {
        return "TimeValueFormatException{" +
                "val='" + val + '\'' +
                '}';
    }
}
