package me.egg82.arr.readarr.v1.model;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.AuthorMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.*;

public class Book extends AbstractAPIObject {
    private final int id;
    private final int authorMetadataId;
    private final String foreignBookId;
    private final String foreignEditionId;
    private final String titleSlug;
    private final String title;
    private final Instant releaseDate;
    private final PVector<@NotNull Links> links;
    private final PSet<@NotNull String> genres;
    private final IntSet relatedBooks = new IntArraySet();
    private final Ratings ratings;
    private final Instant lastSearchTime;
    private final String cleanTitle;
    private final boolean monitored;
    private final boolean anyEditionOk;
    private final Instant lastInfoSync;
    private final Instant added;
    private final AddBookOptions addOptions;
    private final AuthorMetadataLazyLoaded authorMetadata;
    private final AuthorLazyLoaded author;
    private final EditionListLazyLoaded editions;
    private final BookFileListLazyLoaded bookFiles;
    private final SeriesBookLinkListLazyLoaded seriesLinks;

    public Book(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.authorMetadataId = NumberParser.getInt(-1, obj, "authorMetadataId");
        this.foreignBookId = StringParser.get(obj, "foreignBookId");
        this.foreignEditionId = StringParser.get(obj, "foreignEditionId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.title = StringParser.get(obj, "title");
        this.releaseDate = InstantParser.get(obj, "releaseDate");

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull Links> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new Links(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        JSONArray relatedBooks = obj.has("relatedBooks") && obj.get("relatedBooks") != null ? obj.getJSONArray("relatedBooks") : null;
        if (relatedBooks != null) {
            for (int i = 0; i < relatedBooks.length(); i++) {
                this.relatedBooks.add(relatedBooks.getInt(i));
            }
        }

        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.lastSearchTime = InstantParser.get(Instant.EPOCH, obj, "lastSearchTime");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.anyEditionOk = BooleanParser.get(false, obj, "anyEditionOk");
        this.lastInfoSync = InstantParser.get(Instant.EPOCH, obj, "lastInfoSync");
        this.added = InstantParser.get(obj, "added");
        this.addOptions = ObjectParser.get(AddBookOptions.class, api, obj, "addOptions");
        this.authorMetadata = ObjectParser.get(AuthorMetadataLazyLoaded.class, api, obj, "authorMetadata");
        this.author = ObjectParser.get(AuthorLazyLoaded.class, api, obj, "author");
        this.editions = ObjectParser.get(EditionListLazyLoaded.class, api, obj, "editions");
        this.bookFiles = ObjectParser.get(BookFileListLazyLoaded.class, api, obj, "bookFiles");
        this.seriesLinks = ObjectParser.get(SeriesBookLinkListLazyLoaded.class, api, obj, "seriesLinks");
    }

    public int id() {
        return id;
    }

    public @Nullable AuthorMetadata authorMetadata() {
        return api.fetch(AuthorMetadata.class, authorMetadataId);
    }

    public @Nullable String foreignBookId() {
        return foreignBookId;
    }

    public @Nullable String foreignEditionId() {
        return foreignEditionId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @NotNull PVector<me.egg82.arr.readarr.v1.Book> relatedBooks() {
        List<me.egg82.arr.readarr.v1.Book> r = new ArrayList<>();
        for (int id : relatedBooks) {
            r.add(api.fetch(me.egg82.arr.readarr.v1.Book.class, id));
        }
        return TreePVector.from(r);
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @NotNull Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean anyEditionOk() {
        return anyEditionOk;
    }

    public @NotNull Instant lastInfoSync() {
        return lastInfoSync;
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddBookOptions addOptions() {
        return addOptions;
    }

    public @Nullable AuthorMetadataLazyLoaded authorMetadataLazy() {
        return authorMetadata;
    }

    public @Nullable AuthorLazyLoaded author() {
        return author;
    }

    public @Nullable EditionListLazyLoaded editions() {
        return editions;
    }

    public @Nullable BookFileListLazyLoaded bookFiles() {
        return bookFiles;
    }

    public @Nullable SeriesBookLinkListLazyLoaded seriesLinks() {
        return seriesLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", authorMetadataId=" + authorMetadataId +
                ", foreignBookId='" + foreignBookId + '\'' +
                ", foreignEditionId='" + foreignEditionId + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", links=" + links +
                ", genres=" + genres +
                ", relatedBooks=" + relatedBooks +
                ", ratings=" + ratings +
                ", lastSearchTime=" + lastSearchTime +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", monitored=" + monitored +
                ", anyEditionOk=" + anyEditionOk +
                ", lastInfoSync=" + lastInfoSync +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", authorMetadata=" + authorMetadata +
                ", author=" + author +
                ", editions=" + editions +
                ", bookFiles=" + bookFiles +
                ", seriesLinks=" + seriesLinks +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
