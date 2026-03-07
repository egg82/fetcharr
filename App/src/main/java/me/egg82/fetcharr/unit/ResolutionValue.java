package me.egg82.fetcharr.unit;

import java.util.Objects;

public class ResolutionValue {
    private final int horizontal;
    private final int vertical;

    public ResolutionValue(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int horizontal() {
        return horizontal;
    }

    public int vertical() {
        return vertical;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResolutionValue that)) return false;
        return horizontal == that.horizontal && vertical == that.vertical;
    }

    @Override
    public int hashCode() {
        return Objects.hash(horizontal, vertical);
    }

    @Override
    public String toString() {
        return "ResolutionValue{" +
                "horizontal=" + horizontal +
                ", vertical=" + vertical +
                '}';
    }
}
