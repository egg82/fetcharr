package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomFormat extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final boolean includeCustomFormatWhenRenaming;
    private final PVector<@NotNull ICustomFormatSpecification> specifications;

    public CustomFormat(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.includeCustomFormatWhenRenaming = BooleanParser.get(false, obj, "includeCustomFormatWhenRenaming");

        JSONArray specifications = obj.has("specifications") && obj.get("specifications") != null ? obj.getJSONArray("specifications") : null;
        List<@NotNull ICustomFormatSpecification> specificationsL = new ArrayList<>();
        if (specifications != null) {
            for (int i = 0; i < specifications.length(); i++) {
                specificationsL.add(new ICustomFormatSpecification(api, specifications.getJSONObject(i)));
            }
        }
        this.specifications = TreePVector.from(specificationsL);
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public boolean includeCustomFormatWhenRenaming() {
        return includeCustomFormatWhenRenaming;
    }

    public @NotNull PVector<@NotNull ICustomFormatSpecification> specifications() {
        return specifications;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomFormat that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CustomFormat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", includeCustomFormatWhenRenaming=" + includeCustomFormatWhenRenaming +
                ", specifications=" + specifications +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
