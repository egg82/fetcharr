package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeriesResource extends AbstractAPIObject {
    private final int id;
    private final String title;
    private final String description;
    private final PVector<@NotNull SeriesBookLinkResource> links;

    public SeriesResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.title = StringParser.get(obj, "title");
        this.description = StringParser.get(obj, "description");

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull SeriesBookLinkResource> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new SeriesBookLinkResource(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String description() {
        return description;
    }

    public @NotNull PVector<@NotNull SeriesBookLinkResource> links() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeriesResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SeriesResource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", links=" + links +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
