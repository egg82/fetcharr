package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import me.egg82.arr.readarr.v1.Book;
import me.egg82.arr.readarr.v1.Series;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SeriesBookLinkResource extends AbstractAPIObject {
    private final int id;
    private final String position;
    private final int seriesPosition;
    private final int seriesId;
    private final int bookId;

    public SeriesBookLinkResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.position = StringParser.get(obj, "position");
        this.seriesPosition = NumberParser.getInt(-1, obj, "seriesPosition");
        this.seriesId = NumberParser.getInt(-1, obj, "seriesId");
        this.bookId = NumberParser.getInt(-1, obj, "bookId");
    }

    public int id() {
        return id;
    }

    public @Nullable String position() {
        return position;
    }

    public int seriesPosition() {
        return seriesPosition;
    }

    public @Nullable Series series() {
        return api.fetch(Series.class, seriesId);
    }

    public @Nullable Book book() {
        return api.fetch(Book.class, bookId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeriesBookLinkResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SeriesBookLinkResource{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", seriesPosition=" + seriesPosition +
                ", seriesId=" + seriesId +
                ", bookId=" + bookId +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
