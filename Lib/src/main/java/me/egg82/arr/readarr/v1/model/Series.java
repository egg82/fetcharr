package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Series extends AbstractAPIObject {
    private final int id;
    private final String foreignSeriesId;
    private final String title;
    private final String description;
    private final boolean numbered;
    private final int workCount;
    private final int primaryWorkCount;
    private final Object linkItems;
    private final BookListLazyLoaded books;
    private final String foreignAuthorId;

    public Series(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.foreignSeriesId = StringParser.get(obj, "foreignSeriesId");
        this.title = StringParser.get(obj, "title");
        this.description = StringParser.get(obj, "description");
        this.numbered = BooleanParser.get(false, obj, "numbered");
        this.workCount = NumberParser.getInt(-1, obj, "workCount");
        this.primaryWorkCount = NumberParser.getInt(-1, obj, "primaryWorkCount");
        this.linkItems = obj.get("linkItems");
        this.books = ObjectParser.get(BookListLazyLoaded.class, api, obj, "books");
        this.foreignAuthorId = StringParser.get(obj, "foreignAuthorId");
    }

    public int id() {
        return id;
    }

    public @Nullable String foreignSeriesId() {
        return foreignSeriesId;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String description() {
        return description;
    }

    public boolean numbered() {
        return numbered;
    }

    public int workCount() {
        return workCount;
    }

    public int primaryWorkCount() {
        return primaryWorkCount;
    }

    public @Nullable Object linkItems() {
        return linkItems;
    }

    public @Nullable BookListLazyLoaded books() {
        return books;
    }

    public @Nullable String foreignAuthorId() {
        return foreignAuthorId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Series series)) return false;
        return id == series.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Series{" +
                "id=" + id +
                ", foreignSeriesId='" + foreignSeriesId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", numbered=" + numbered +
                ", workCount=" + workCount +
                ", primaryWorkCount=" + primaryWorkCount +
                ", linkItems=" + linkItems +
                ", books=" + books +
                ", foreignAuthorId='" + foreignAuthorId + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
