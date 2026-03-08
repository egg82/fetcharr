package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.web.model.sonarr.SonarrQualitySource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Quality {
    private final int id;

    private final String name;
    private final int resolution;
    private final SonarrQualitySource source;

    public Quality(@NotNull JSONObject obj) {
        this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
        this.name = StringParser.parse(obj, "name");
        this.resolution = NumberParser.parseInt(-1, StringParser.parse(obj, "resolution"));
        this.source = SonarrQualitySource.parse(SonarrQualitySource.UNKNOWN, StringParser.parse(obj, "source"));
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public int resolution() {
        return resolution;
    }

    public @NotNull SonarrQualitySource source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quality quality)) return false;
        return id == quality.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Quality{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resolution=" + resolution +
                ", source=" + source +
                '}';
    }
}
