package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class QualityModel extends AbstractAPIObject {
    private final Quality quality;
    private final Revision revision;

    public QualityModel(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.quality = ObjectParser.get(Quality.class, api, obj, "quality");
        this.revision = ObjectParser.get(Revision.class, api, obj, "revision");
    }

    public @Nullable Quality quality() {
        return quality;
    }

    public @Nullable Revision revision() {
        return revision;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityModel that)) return false;
        return Objects.equals(quality, that.quality) && Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, revision);
    }

    @Override
    public String toString() {
        return "QualityModel{" +
                "quality=" + quality +
                ", revision=" + revision +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
