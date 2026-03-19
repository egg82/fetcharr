package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Member extends AbstractAPIObject {
    private final String name;
    private final String instrument;
    private final List<@NotNull MediaCover> images = new ArrayList<>();

    public Member(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.name = StringParser.get(obj, "name");
        this.instrument = StringParser.get(obj, "instrument");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String instrument() {
        return instrument;
    }

    public @NotNull List<@NotNull MediaCover> images() {
        return images;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member member)) return false;
        return Objects.equals(name, member.name) && Objects.equals(instrument, member.instrument) && Objects.equals(images, member.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, instrument, images);
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", instrument='" + instrument + '\'' +
                ", images=" + images +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
