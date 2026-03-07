package me.egg82.fetcharr.ex;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ResolutionValueFormatException extends RuntimeException {
    private final String val;

    public ResolutionValueFormatException(@NotNull String val) {
        this.val = val;
    }

    public @NotNull String val() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResolutionValueFormatException that)) return false;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }

    @Override
    public String toString() {
        return "ResolutionValueFormatException{" +
                "val='" + val + '\'' +
                '}';
    }
}
