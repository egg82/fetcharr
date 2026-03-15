package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectOption extends AbstractAPIObject {
    private final int value;
    private final String name;
    private final int order;
    private final String hint;
    private final boolean dividerAfter;

    public SelectOption(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.value = NumberParser.getInt(-1, obj, "value");
        this.name = StringParser.get(obj, "name");
        this.order = NumberParser.getInt(-1, obj, "order");
        this.hint = StringParser.get(obj, "hint");
        this.dividerAfter = BooleanParser.get(false, obj, "dividerAfter");
    }

    public int value() {
        return value;
    }

    public @Nullable String name() {
        return name;
    }

    public int order() {
        return order;
    }

    public @Nullable String hint() {
        return hint;
    }

    public boolean dividerAfter() {
        return dividerAfter;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SelectOption that)) return false;
        return value == that.value && order == that.order && dividerAfter == that.dividerAfter && Objects.equals(name, that.name) && Objects.equals(hint, that.hint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, name, order, hint, dividerAfter);
    }

    @Override
    public String toString() {
        return "SelectOption{" +
                "value=" + value +
                ", name='" + name + '\'' +
                ", order=" + order +
                ", hint='" + hint + '\'' +
                ", dividerAfter=" + dividerAfter +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
