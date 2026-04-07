package me.egg82.arr.readarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.MetadataProfile;
import me.egg82.arr.readarr.v1.QualityProfile;
import me.egg82.arr.readarr.v1.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class AuthorResource extends AbstractAPIObject {
    private final int id;
    private final int authorMetadataId;
    private final AuthorStatusType status;
    private final boolean ended;
    private final String authorName;
    private final String authorNameLastFirst;
    private final String foreignAuthorId;
    private final String titleSlug;
    private final String overview;
    private final String disambiguation;
    private final PVector<@NotNull Links> links;
    private final Book nextBook;
    private final Book lastBook;
    private final PVector<@NotNull MediaCover> images;
    private final String remotePoster;
    private final File path;
    private final int qualityProfileId;
    private final int metadataProfileId;
    private final boolean monitored;
    private final NewItemMonitorTypes monitorNewItems;
    private final File rootFolderPath;
    private final String folder;
    private final PSet<@NotNull String> genres;
    private final String cleanName;
    private final String sortName;
    private final String sortNameLastFirst;
    private final IntSet tags = new IntArraySet();
    private final Instant added;
    private final AddAuthorOptions addOptions;
    private final Ratings ratings;
    private final AuthorStatisticsResource statistics;

    public AuthorResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.authorMetadataId = NumberParser.getInt(-1, obj, "authorMetadataId");
        this.status = AuthorStatusType.get(AuthorStatusType.CONTINUING, obj, "status");
        this.ended = BooleanParser.get(false, obj, "ended");
        this.authorName = StringParser.get(obj, "authorName");
        this.authorNameLastFirst = StringParser.get(obj, "authorNameLastFirst");
        this.foreignAuthorId = StringParser.get(obj, "foreignAuthorId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.overview = StringParser.get(obj, "overview");
        this.disambiguation = StringParser.get(obj, "disambiguation");

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull Links> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new Links(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);

        this.nextBook = ObjectParser.get(Book.class, api, obj, "nextBook");
        this.lastBook = ObjectParser.get(Book.class, api, obj, "lastBook");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        this.remotePoster = StringParser.get(obj, "remotePoster");
        this.path = FileParser.get(obj, "path");
        this.qualityProfileId = NumberParser.getInt(-1, obj, "qualityProfileId");
        this.metadataProfileId = NumberParser.getInt(-1, obj, "metadataProfileId");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.monitorNewItems = NewItemMonitorTypes.get(NewItemMonitorTypes.NONE, obj, "monitorNewItems");
        this.rootFolderPath = FileParser.get(obj, "rootFolderPath");
        this.folder = StringParser.get(obj, "folder");

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        this.cleanName = StringParser.get(obj, "cleanName");
        this.sortName = StringParser.get(obj, "sortName");
        this.sortNameLastFirst = StringParser.get(obj, "sortNameLastFirst");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        this.added = InstantParser.get(obj, "added");
        this.addOptions = ObjectParser.get(AddAuthorOptions.class, api, obj, "addOptions");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.statistics = ObjectParser.get(AuthorStatisticsResource.class, api, obj, "statistics");
    }

    public int id() {
        return id;
    }

    public int authorMetadataId() {
        return authorMetadataId;
    }

    public @Nullable AuthorStatusType status() {
        return status;
    }

    public boolean ended() {
        return ended;
    }

    public @Nullable String authorName() {
        return authorName;
    }

    public @Nullable String authorNameLastFirst() {
        return authorNameLastFirst;
    }

    public @Nullable String foreignAuthorId() {
        return foreignAuthorId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @Nullable Book nextBook() {
        return nextBook;
    }

    public @Nullable Book lastBook() {
        return lastBook;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityProfile qualityProfile() {
        return api.fetch(QualityProfile.class, qualityProfileId);
    }

    public @Nullable MetadataProfile metadataProfile() {
        return api.fetch(MetadataProfile.class, metadataProfileId);
    }

    public boolean monitored() {
        return monitored;
    }

    public @Nullable NewItemMonitorTypes monitorNewItems() {
        return monitorNewItems;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable String folder() {
        return folder;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @Nullable String cleanName() {
        return cleanName;
    }

    public @Nullable String sortName() {
        return sortName;
    }

    public @Nullable String sortNameLastFirst() {
        return sortNameLastFirst;
    }

    public @NotNull PVector<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return TreePVector.from(r);
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddAuthorOptions addOptions() {
        return addOptions;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable AuthorStatisticsResource statistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthorResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AuthorResource{" +
                "id=" + id +
                ", authorMetadataId=" + authorMetadataId +
                ", status=" + status +
                ", ended=" + ended +
                ", authorName='" + authorName + '\'' +
                ", authorNameLastFirst='" + authorNameLastFirst + '\'' +
                ", foreignAuthorId='" + foreignAuthorId + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", overview='" + overview + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", links=" + links +
                ", nextBook=" + nextBook +
                ", lastBook=" + lastBook +
                ", images=" + images +
                ", remotePoster='" + remotePoster + '\'' +
                ", path=" + path +
                ", qualityProfileId=" + qualityProfileId +
                ", metadataProfileId=" + metadataProfileId +
                ", monitored=" + monitored +
                ", monitorNewItems=" + monitorNewItems +
                ", rootFolderPath=" + rootFolderPath +
                ", folder='" + folder + '\'' +
                ", genres=" + genres +
                ", cleanName='" + cleanName + '\'' +
                ", sortName='" + sortName + '\'' +
                ", sortNameLastFirst='" + sortNameLastFirst + '\'' +
                ", tags=" + tags +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", ratings=" + ratings +
                ", statistics=" + statistics +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
