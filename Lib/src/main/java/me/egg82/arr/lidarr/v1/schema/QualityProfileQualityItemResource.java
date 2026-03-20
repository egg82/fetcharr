package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QualityProfileQualityItemResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final Quality quality;
    private final PVector<@NotNull Object> items;
    private final boolean allowed;

    public QualityProfileQualityItemResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.quality = ObjectParser.get(Quality.class, api, obj, "quality");

        JSONArray items = obj.has("items") && obj.get("items") != null ? obj.getJSONArray("items") : null;
        List<@NotNull Object> itemsL = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                itemsL.add(items.get(i));
            }
        }
        this.items = TreePVector.from(itemsL);

        this.allowed = BooleanParser.get(false, obj, "allowed");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable Quality quality() {
        return quality;
    }

    public @NotNull PVector<@NotNull Object> items() {
        return items;
    }

    public boolean allowed() {
        return allowed;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfileQualityItemResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityProfileQualityItemResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quality=" + quality +
                ", items=" + items +
                ", allowed=" + allowed +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
