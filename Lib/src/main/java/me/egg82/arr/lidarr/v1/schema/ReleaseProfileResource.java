package me.egg82.arr.lidarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Indexer;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.util.*;

public class ReleaseProfileResource extends AbstractAPIObject {
    private final int id;
    private final boolean enabled;
    private final PSet<@NotNull String> required;
    private final PSet<@NotNull String> ignored;
    private final int indexerId;
    private final IntSet tags = new IntArraySet();

    public ReleaseProfileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.enabled = BooleanParser.get(false, obj, "enabled");

        JSONArray required = obj.has("required") && obj.get("required") != null ? obj.getJSONArray("required") : null;
        Set<@NotNull String> requiredL = new HashSet<>();
        if (required != null) {
            for (int i = 0; i < required.length(); i++) {
                requiredL.add(required.getString(i));
            }
        }
        this.required = TreePSet.from(requiredL);

        JSONArray ignored = obj.has("ignored") && obj.get("ignored") != null ? obj.getJSONArray("ignored") : null;
        Set<@NotNull String> ignoredL = new HashSet<>();
        if (ignored != null) {
            for (int i = 0; i < ignored.length(); i++) {
                ignoredL.add(ignored.getString(i));
            }
        }
        this.ignored = TreePSet.from(ignoredL);

        this.indexerId = NumberParser.getInt(-1, obj, "indexerId");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }
    }

    public int id() {
        return id;
    }

    public boolean enabled() {
        return enabled;
    }

    public @NotNull PSet<@NotNull String> required() {
        return required;
    }

    public @NotNull PSet<@NotNull String> ignored() {
        return ignored;
    }

    public @Nullable Indexer indexer() {
        return api.fetch(Indexer.class, indexerId);
    }

    public @NotNull PVector<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return TreePVector.from(r);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReleaseProfileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ReleaseProfileResource{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", required=" + required +
                ", ignored=" + ignored +
                ", indexerId=" + indexerId +
                ", tags=" + tags +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
