package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.Book;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edition extends AbstractAPIObject {
    private final int id;
    private final int bookId;
    private final String foreignEditionId;
    private final String titleSlug;
    private final String isbn13;
    private final String asin;
    private final String title;
    private final String language;
    private final String overview;
    private final String format;
    private final boolean isEbook;
    private final String disambiguation;
    private final String publisher;
    private final int pageCount;
    private final Instant releaseDate;
    private final PVector<@NotNull MediaCover> images;
    private final PVector<@NotNull Links> links;
    private final Ratings ratings;
    private final boolean monitored;
    private final boolean manualAdd;
    private final BookLazyLoaded book;
    private final BookFileListLazyLoaded bookFiles;

    public Edition(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.bookId = NumberParser.getInt(-1, obj, "bookId");
        this.foreignEditionId = StringParser.get(obj, "foreignEditionId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.isbn13 = StringParser.get(obj, "isbn13");
        this.asin = StringParser.get(obj, "asin");
        this.title = StringParser.get(obj, "title");
        this.language = StringParser.get(obj, "language");
        this.overview = StringParser.get(obj, "overview");
        this.format = StringParser.get(obj, "format");
        this.isEbook = BooleanParser.get(false, obj, "isEbook");
        this.disambiguation = StringParser.get(obj, "disambiguation");
        this.publisher = StringParser.get(obj, "publisher");
        this.pageCount = NumberParser.getInt(-1, obj, "pageCount");
        this.releaseDate = InstantParser.get(obj, "releaseDate");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull Links> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new Links(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);

        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.manualAdd = BooleanParser.get(false, obj, "manualAdd");
        this.book = ObjectParser.get(BookLazyLoaded.class, api, obj, "book");
        this.bookFiles = ObjectParser.get(BookFileListLazyLoaded.class, api, obj, "bookFiles");
    }

    public int id() {
        return id;
    }

    public @Nullable Book book() {
        return api.fetch(Book.class, bookId);
    }

    public @Nullable String foreignEditionId() {
        return foreignEditionId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable String isbn13() {
        return isbn13;
    }

    public @Nullable String asin() {
        return asin;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String language() {
        return language;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable String format() {
        return format;
    }

    public boolean isEbook() {
        return isEbook;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable String publisher() {
        return publisher;
    }

    public int pageCount() {
        return pageCount;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean manualAdd() {
        return manualAdd;
    }

    public @Nullable BookLazyLoaded bookLazy() {
        return book;
    }

    public @Nullable BookFileListLazyLoaded bookFiles() {
        return bookFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edition edition)) return false;
        return id == edition.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Edition{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", foreignEditionId='" + foreignEditionId + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", asin='" + asin + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", overview='" + overview + '\'' +
                ", format='" + format + '\'' +
                ", isEbook=" + isEbook +
                ", disambiguation='" + disambiguation + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pageCount=" + pageCount +
                ", releaseDate=" + releaseDate +
                ", images=" + images +
                ", links=" + links +
                ", ratings=" + ratings +
                ", monitored=" + monitored +
                ", manualAdd=" + manualAdd +
                ", book=" + book +
                ", bookFiles=" + bookFiles +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
