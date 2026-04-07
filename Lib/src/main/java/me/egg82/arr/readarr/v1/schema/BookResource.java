package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.*;

public class BookResource extends AbstractAPIObject {
    private final int id;
    private final String title;
    private final String authorTitle;
    private final String seriesTitle;
    private final String disambiguation;
    private final String overview;
    private final String foreignBookId;
    private final String foreignEditionId;
    private final String titleSlug;
    private final boolean monitored;
    private final boolean anyEditionOk;
    private final Ratings ratings;
    private final Instant releaseDate;
    private final int pageCount;
    private final PSet<@NotNull String> genres;
    private final AuthorResource author;
    private final PVector<@NotNull MediaCover> images;
    private final PVector<@NotNull Links> links;
    private final BookStatisticsResource statistics;
    private final Instant added;
    private final AddBookOptions addOptions;
    private final String remoteCover;
    private final Instant lastSearchTime;
    private final PVector<@NotNull EditionResource> editions;

    public BookResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.title = StringParser.get(obj, "title");
        this.authorTitle = StringParser.get(obj, "authorTitle");
        this.seriesTitle = StringParser.get(obj, "seriesTitle");
        this.disambiguation = StringParser.get(obj, "disambiguation");
        this.overview = StringParser.get(obj, "overview");
        this.foreignBookId = StringParser.get(obj, "foreignBookId");
        this.foreignEditionId = StringParser.get(obj, "foreignEditionId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.anyEditionOk = BooleanParser.get(false, obj, "anyEditionOk");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.releaseDate = InstantParser.get(obj, "releaseDate");
        this.pageCount = NumberParser.getInt(-1, obj, "pageCount");

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        this.author = ObjectParser.get(AuthorResource.class, api, obj, "author");

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

        this.statistics = ObjectParser.get(BookStatisticsResource.class, api, obj, "statistics");
        this.added = InstantParser.get(obj, "added");
        this.addOptions = ObjectParser.get(AddBookOptions.class, api, obj, "addOptions");
        this.remoteCover = StringParser.get(obj, "remoteCover");
        this.lastSearchTime = InstantParser.get(obj, "lastSearchTime");

        JSONArray editions = obj.has("editions") && obj.get("editions") != null ? obj.getJSONArray("editions") : null;
        List<@NotNull EditionResource> editionsL = new ArrayList<>();
        if (editions != null) {
            for (int i = 0; i < editions.length(); i++) {
                editionsL.add(new EditionResource(api, editions.getJSONObject(i)));
            }
        }
        this.editions = TreePVector.from(editionsL);
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String authorTitle() {
        return authorTitle;
    }

    public @Nullable String seriesTitle() {
        return seriesTitle;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable String overview() {
        return overview;
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

    public boolean monitored() {
        return monitored;
    }

    public boolean anyEditionOk() {
        return anyEditionOk;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public int pageCount() {
        return pageCount;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @Nullable AuthorResource author() {
        return author;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @Nullable BookStatisticsResource statistics() {
        return statistics;
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddBookOptions addOptions() {
        return addOptions;
    }

    public @Nullable String remoteCover() {
        return remoteCover;
    }

    public @Nullable Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @NotNull PVector<@NotNull EditionResource> editions() {
        return editions;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BookResource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authorTitle='" + authorTitle + '\'' +
                ", seriesTitle='" + seriesTitle + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", overview='" + overview + '\'' +
                ", foreignBookId='" + foreignBookId + '\'' +
                ", foreignEditionId='" + foreignEditionId + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", monitored=" + monitored +
                ", anyEditionOk=" + anyEditionOk +
                ", ratings=" + ratings +
                ", releaseDate=" + releaseDate +
                ", pageCount=" + pageCount +
                ", genres=" + genres +
                ", author=" + author +
                ", images=" + images +
                ", links=" + links +
                ", statistics=" + statistics +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", remoteCover='" + remoteCover + '\'' +
                ", lastSearchTime=" + lastSearchTime +
                ", editions=" + editions +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
