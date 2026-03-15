package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QualityModel extends AbstractAPIObject {
    private final Quality quality;
    private final Revision revision;

    public QualityModel(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.quality = new Quality(api, obj.getJSONObject("quality"));
        this.revision = new Revision(api, obj.getJSONObject("revision"));
    }

    public @NotNull Quality quality() {
        return quality;
    }

    public @NotNull Revision revision() {
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
