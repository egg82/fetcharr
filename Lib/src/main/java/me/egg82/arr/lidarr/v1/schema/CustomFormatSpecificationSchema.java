package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomFormatSpecificationSchema extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final String implementation;
    private final String implementationName;
    private final String infoLink;
    private final boolean negate;
    private final boolean required;
    private final List<@NotNull Field> fields = new ArrayList<>();
    private final List<@NotNull Object> presets = new ArrayList<>();

    public CustomFormatSpecificationSchema(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.implementation = StringParser.get(obj, "implementation");
        this.implementationName = StringParser.get(obj, "implementationName");
        this.infoLink = StringParser.get(obj, "infoLink");
        this.negate = BooleanParser.get(false, obj, "negate");
        this.required = BooleanParser.get(false, obj, "required");

        JSONArray fields = obj.has("fields") && obj.get("fields") != null ? obj.getJSONArray("fields") : null;
        if (fields != null) {
            for (int i = 0; i < fields.length(); i++) {
                this.fields.add(new Field(api, fields.getJSONObject(i)));
            }
        }

        JSONArray presets = obj.has("presets") && obj.get("presets") != null ? obj.getJSONArray("presets") : null;
        if (presets != null) {
            for (int i = 0; i < presets.length(); i++) {
                this.presets.add(presets.get(i));
            }
        }
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String implementation() {
        return implementation;
    }

    public @Nullable String implementationName() {
        return implementationName;
    }

    public @Nullable String infoLink() {
        return infoLink;
    }

    public boolean negate() {
        return negate;
    }

    public boolean required() {
        return required;
    }

    public @NotNull List<@NotNull Field> fields() {
        return fields;
    }

    public @NotNull List<@NotNull Object> presets() {
        return presets;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomFormatSpecificationSchema that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CustomFormatSpecificationSchema{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", implementation='" + implementation + '\'' +
                ", implementationName='" + implementationName + '\'' +
                ", infoLink='" + infoLink + '\'' +
                ", negate=" + negate +
                ", required=" + required +
                ", fields=" + fields +
                ", presets=" + presets +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
