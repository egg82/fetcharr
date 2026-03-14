package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Link {
    private final String url;
    private final String name;

    public Link(@NotNull JSONObject obj) {
        this.url = StringParser.parse(obj, "url");
        this.name = StringParser.parse(obj, "name");
    }

    public @Nullable String url() {
        return url;
    }

    public @Nullable String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Link link)) return false;
        return Objects.equals(url, link.url) && Objects.equals(name, link.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name);
    }

    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
