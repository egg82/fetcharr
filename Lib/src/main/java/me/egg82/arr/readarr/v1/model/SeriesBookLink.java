package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import me.egg82.arr.readarr.v1.Book;
import me.egg82.arr.readarr.v1.Series;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SeriesBookLink extends AbstractAPIObject {
    private final int id;
    private final String position;
    private final int seriesPosition;
    private final int seriesId;
    private final int bookId;
    private final boolean isPrimary;
    private final SeriesLazyLoaded series;
    private final BookLazyLoaded book;

    public SeriesBookLink(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.position = StringParser.get(obj, "position");
        this.seriesPosition = NumberParser.getInt(-1, obj, "seriesPosition");
        this.seriesId = NumberParser.getInt(-1, obj, "seriesId");
        this.bookId = NumberParser.getInt(-1, obj, "bookId");
        this.isPrimary = BooleanParser.get(false, obj, "isPrimary");
        this.series = ObjectParser.get(SeriesLazyLoaded.class, api, obj, "series");
        this.book = ObjectParser.get(BookLazyLoaded.class, api, obj, "book");
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

    public boolean isPrimary() {
        return isPrimary;
    }

    public @Nullable SeriesLazyLoaded seriesLazy() {
        return series;
    }

    public @Nullable BookLazyLoaded bookLazy() {
        return book;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeriesBookLink that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SeriesBookLink{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", seriesPosition=" + seriesPosition +
                ", seriesId=" + seriesId +
                ", bookId=" + bookId +
                ", isPrimary=" + isPrimary +
                ", series=" + series +
                ", book=" + book +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
