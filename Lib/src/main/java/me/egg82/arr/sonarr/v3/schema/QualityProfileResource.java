package me.egg82.arr.sonarr.v3.schema;

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

public class QualityProfileResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final boolean upgradeAllowed;
    private final int cutoff;
    private final PVector<@NotNull QualityProfileQualityItemResource> items;
    private final int minFormatScore;
    private final int cutoffFormatScore;
    private final int minUpgradeFormatScore;
    private final PVector<@NotNull ProfileFormatItemResource> formatItems;
    private final Language language;

    public QualityProfileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.upgradeAllowed = BooleanParser.get(false, obj, "upgradeAllowed");
        this.cutoff = NumberParser.getInt(-1, obj, "cutoff");

        JSONArray items = obj.has("items") && obj.get("items") != null ? obj.getJSONArray("items") : null;
        List<@NotNull QualityProfileQualityItemResource> itemsL = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                itemsL.add(new QualityProfileQualityItemResource(api, items.getJSONObject(i)));
            }
        }
        this.items = TreePVector.from(itemsL);

        this.minFormatScore = NumberParser.getInt(-1, obj, "minFormatScore");
        this.cutoffFormatScore = NumberParser.getInt(-1, obj, "cutoffFormatScore");
        this.minUpgradeFormatScore = NumberParser.getInt(-1, obj, "minUpgradeFormatScore");

        JSONArray formatItems = obj.has("formatItems") && obj.get("formatItems") != null ? obj.getJSONArray("formatItems") : null;
        List<@NotNull ProfileFormatItemResource> formatItemsL = new ArrayList<>();
        if (formatItems != null) {
            for (int i = 0; i < formatItems.length(); i++) {
                formatItemsL.add(new ProfileFormatItemResource(api, formatItems.getJSONObject(i)));
            }
        }
        this.formatItems = TreePVector.from(formatItemsL);

        this.language = ObjectParser.get(Language.class, api, obj, "language");
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

    public @NotNull PVector<@NotNull QualityProfileQualityItemResource> items() {
        return items;
    }

    public int minFormatScore() {
        return minFormatScore;
    }

    public int cutoffFormatScore() {
        return cutoffFormatScore;
    }

    public int minUpgradeFormatScore() {
        return minUpgradeFormatScore;
    }

    public @NotNull PVector<@NotNull ProfileFormatItemResource> formatItems() {
        return formatItems;
    }

    public @Nullable Language language() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityProfileResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", upgradeAllowed=" + upgradeAllowed +
                ", cutoff=" + cutoff +
                ", items=" + items +
                ", minFormatScore=" + minFormatScore +
                ", cutoffFormatScore=" + cutoffFormatScore +
                ", minUpgradeFormatScore=" + minUpgradeFormatScore +
                ", formatItems=" + formatItems +
                ", language=" + language +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
