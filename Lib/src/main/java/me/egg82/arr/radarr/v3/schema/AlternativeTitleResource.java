package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import me.egg82.arr.common.AbstractAPIObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AlternativeTitleResource extends AbstractAPIObject {
    private final int id;
    private final SourceType sourceType;
    private final int movieMetadataId;
    private final String title;
    private final String cleanTitle;

    public AlternativeTitleResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.sourceType = SourceType.get(SourceType.USER, obj, "sourceType");
        this.movieMetadataId = NumberParser.getInt(-1, obj, "movieMetadataId");
        this.title = StringParser.get(obj, "title");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");
    }

    public int id() {
        return id;
    }

    public @NotNull SourceType sourceType() {
        return sourceType;
    }

    public int movieMetadataId() {
        return movieMetadataId;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlternativeTitleResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlternativeTitleResource{" +
                "id=" + id +
                ", sourceType=" + sourceType +
                ", movieMetadataId=" + movieMetadataId +
                ", title='" + title + '\'' +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
