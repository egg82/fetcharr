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
import org.pcollections.PSet;
import org.pcollections.TreePSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MetadataProfile extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final float minPopularity;
    private final boolean skipMissingDate;
    private final boolean skipMissingIsbn;
    private final boolean skipPartsAndSets;
    private final boolean skipSeriesSecondary;
    private final PSet<@NotNull String> allowedLanguages;
    private final int minPages;
    private final PSet<@NotNull String> ignored;

    public MetadataProfile(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");
        this.minPopularity = NumberParser.getFloat(-1.0F, obj, "minPopularity");
        this.skipMissingDate = BooleanParser.get(false, obj, "skipMissingDate");
        this.skipMissingIsbn = BooleanParser.get(false, obj, "skipMissingIsbn");
        this.skipPartsAndSets = BooleanParser.get(false, obj, "skipPartsAndSets");
        this.skipSeriesSecondary = BooleanParser.get(false, obj, "skipSeriesSecondary");

        String allowedLanguages = StringParser.get(obj, "allowedLanguages");
        this.allowedLanguages = allowedLanguages != null ? TreePSet.from(Arrays.asList(allowedLanguages.trim().split(","))) : TreePSet.empty();

        this.minPages = NumberParser.getInt(-1, obj, "minPages");

        JSONArray ignored = obj.has("ignored") && obj.get("ignored") != null ? obj.getJSONArray("ignored") : null;
        Set<@NotNull String> ignoredL = new HashSet<>();
        if (ignored != null) {
            for (int i = 0; i < ignored.length(); i++) {
                ignoredL.add(ignored.getString(i));
            }
        }
        this.ignored = TreePSet.from(ignoredL);
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public float minPopularity() {
        return minPopularity;
    }

    public boolean skipMissingDate() {
        return skipMissingDate;
    }

    public boolean skipMissingIsbn() {
        return skipMissingIsbn;
    }

    public boolean skipPartsAndSets() {
        return skipPartsAndSets;
    }

    public boolean skipSeriesSecondary() {
        return skipSeriesSecondary;
    }

    public @NotNull PSet<@NotNull String> allowedLanguages() {
        return allowedLanguages;
    }

    public int minPages() {
        return minPages;
    }

    public @NotNull PSet<@NotNull String> ignored() {
        return ignored;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MetadataProfile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MetadataProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", minPopularity=" + minPopularity +
                ", skipMissingDate=" + skipMissingDate +
                ", skipMissingIsbn=" + skipMissingIsbn +
                ", skipPartsAndSets=" + skipPartsAndSets +
                ", skipSeriesSecondary=" + skipSeriesSecondary +
                ", allowedLanguages=" + allowedLanguages +
                ", minPages=" + minPages +
                ", ignored=" + ignored +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
