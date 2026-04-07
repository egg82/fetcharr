package me.egg82.arr.readarr.v1.schema;

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

public class QualityProfile extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final boolean upgradeAllowed;
    private final int cutoff;
    private final int minFormatScore;
    private final int cutoffFormatScore;
    private final PVector<@NotNull ProfileFormatItem> formatItems;
    private final PVector<@NotNull QualityProfileQualityItem> items;

    public QualityProfile(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.upgradeAllowed = BooleanParser.get(false, obj, "upgradeAllowed");
        this.cutoff = NumberParser.getInt(-1, obj, "cutoff");
        this.minFormatScore = NumberParser.getInt(-1, obj, "minFormatScore");
        this.cutoffFormatScore = NumberParser.getInt(-1, obj, "cutoffFormatScore");

        JSONArray formatItems = obj.has("formatItems") && obj.get("formatItems") != null ? obj.getJSONArray("formatItems") : null;
        List<@NotNull ProfileFormatItem> formatItemsL = new ArrayList<>();
        if (formatItems != null) {
            for (int i = 0; i < formatItems.length(); i++) {
                formatItemsL.add(new ProfileFormatItem(api, formatItems.getJSONObject(i)));
            }
        }
        this.formatItems = TreePVector.from(formatItemsL);

        JSONArray items = obj.has("items") && obj.get("items") != null ? obj.getJSONArray("items") : null;
        List<@NotNull QualityProfileQualityItem> itemsL = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                itemsL.add(new QualityProfileQualityItem(api, items.getJSONObject(i)));
            }
        }
        this.items = TreePVector.from(itemsL);
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public boolean upgradeAllowed() {
        return upgradeAllowed;
    }

    public int cutoff() {
        return cutoff;
    }

    public int minFormatScore() {
        return minFormatScore;
    }

    public int cutoffFormatScore() {
        return cutoffFormatScore;
    }

    public @NotNull PVector<@NotNull ProfileFormatItem> formatItems() {
        return formatItems;
    }

    public @NotNull PVector<@NotNull QualityProfileQualityItem> items() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", upgradeAllowed=" + upgradeAllowed +
                ", cutoff=" + cutoff +
                ", minFormatScore=" + minFormatScore +
                ", cutoffFormatScore=" + cutoffFormatScore +
                ", formatItems=" + formatItems +
                ", items=" + items +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
