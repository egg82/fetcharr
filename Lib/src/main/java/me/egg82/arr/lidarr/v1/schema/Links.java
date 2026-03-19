package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Links extends AbstractAPIObject {
    private final String url;
    private final String name;

    public Links(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.url = StringParser.get(obj, "url");
        this.name = StringParser.get(obj, "name");
    }

    public @Nullable String url() {
        return url;
    }

    public @Nullable String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Links links)) return false;
        return Objects.equals(url, links.url) && Objects.equals(name, links.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name);
    }

    @Override
    public String toString() {
        return "Links{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
