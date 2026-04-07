package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ICustomFormatSpecification extends AbstractAPIObject {
    private final int order;
    private final String infoLink;
    private final String implementationName;
    private final String name;
    private final boolean negate;
    private final boolean required;

    public ICustomFormatSpecification(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.order = NumberParser.getInt(-1, obj, "order");
        this.infoLink = StringParser.get(obj, "infoLink");
        this.implementationName = StringParser.get(obj, "implementationName");
        this.name = StringParser.get(obj, "name");
        this.negate = BooleanParser.get(false, obj, "negate");
        this.required = BooleanParser.get(false, obj, "required");
    }

    public int order() {
        return order;
    }

    public @Nullable String infoLink() {
        return infoLink;
    }

    public @Nullable String implementationName() {
        return implementationName;
    }

    public @Nullable String name() {
        return name;
    }

    public boolean negate() {
        return negate;
    }

    public boolean required() {
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ICustomFormatSpecification that)) return false;
        return order == that.order && negate == that.negate && required == that.required && Objects.equals(infoLink, that.infoLink) && Objects.equals(implementationName, that.implementationName) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, infoLink, implementationName, name, negate, required);
    }

    @Override
    public String toString() {
        return "ICustomFormatSpecification{" +
                "order=" + order +
                ", infoLink='" + infoLink + '\'' +
                ", implementationName='" + implementationName + '\'' +
                ", name='" + name + '\'' +
                ", negate=" + negate +
                ", required=" + required +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
