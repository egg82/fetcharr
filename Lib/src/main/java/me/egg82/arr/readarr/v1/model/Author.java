package me.egg82.arr.readarr.v1.model;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.AuthorMetadata;
import me.egg82.arr.readarr.v1.MetadataProfile;
import me.egg82.arr.readarr.v1.QualityProfile;
import me.egg82.arr.readarr.v1.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Author extends AbstractAPIObject {
    private final int id;
    private final int authorMetadataId;
    private final String cleanName;
    private final boolean monitored;
    private final NewItemMonitorTypes monitorNewItems;
    private final Instant lastInfoSync;
    private final File path;
    private final File rootFolderPath;
    private final Instant added;
    private final int qualityProfileId;
    private final int metadataProfileId;
    private final IntSet tags = new IntArraySet();
    private final AddAuthorOptions addOptions;
    private final AuthorMetadataLazyLoaded metadata;
    private final QualityProfileLazyLoaded qualityProfile;
    private final MetadataProfileLazyLoaded metadataProfile;
    private final BookListLazyLoaded books;
    private final SeriesListLazyLoaded series;
    private final String name;
    private final String foreignAuthorId;

    public Author(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.authorMetadataId = NumberParser.getInt(-1, obj, "authorMetadataId");
        this.cleanName = StringParser.get(obj, "cleanName");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.monitorNewItems = NewItemMonitorTypes.get(NewItemMonitorTypes.NONE, obj, "monitorNewItems");
        this.lastInfoSync = InstantParser.get(Instant.EPOCH, obj, "lastInfoSync");
        this.path = FileParser.get(obj, "path");
        this.rootFolderPath = FileParser.get(obj, "rootFolderPath");
        this.added = InstantParser.get(obj, "added");
        this.qualityProfileId = NumberParser.getInt(-1, obj, "qualityProfileId");
        this.metadataProfileId = NumberParser.getInt(-1, obj, "metadataProfileId");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        this.addOptions = ObjectParser.get(AddAuthorOptions.class, api, obj, "addOptions");
        this.metadata = ObjectParser.get(AuthorMetadataLazyLoaded.class, api, obj, "metadata");
        this.qualityProfile = ObjectParser.get(QualityProfileLazyLoaded.class, api, obj, "qualityProfile");
        this.metadataProfile = ObjectParser.get(MetadataProfileLazyLoaded.class, api, obj, "metadataProfile");
        this.books = ObjectParser.get(BookListLazyLoaded.class, api, obj, "books");
        this.series = ObjectParser.get(SeriesListLazyLoaded.class, api, obj, "series");
        this.name = StringParser.get(obj, "name");
        this.foreignAuthorId = StringParser.get(obj, "foreignAuthorId");
    }

    public int id() {
        return id;
    }

    public @Nullable AuthorMetadata authorMetadata() {
        return api.fetch(AuthorMetadata.class, authorMetadataId);
    }

    public @Nullable String cleanName() {
        return cleanName;
    }

    public boolean monitored() {
        return monitored;
    }

    public @Nullable NewItemMonitorTypes monitorNewItems() {
        return monitorNewItems;
    }

    public @NotNull Instant lastInfoSync() {
        return lastInfoSync;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable QualityProfile qualityProfile() {
        return api.fetch(QualityProfile.class, qualityProfileId);
    }

    public @Nullable MetadataProfile metadataProfile() {
        return api.fetch(MetadataProfile.class, metadataProfileId);
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

    public @Nullable AddAuthorOptions addOptions() {
        return addOptions;
    }

    public @Nullable AuthorMetadataLazyLoaded metadata() {
        return metadata;
    }

    public @Nullable QualityProfileLazyLoaded qualityProfileLazy() {
        return qualityProfile;
    }

    public @Nullable MetadataProfileLazyLoaded metadataProfileLazy() {
        return metadataProfile;
    }

    public @Nullable BookListLazyLoaded books() {
        return books;
    }

    public @Nullable SeriesListLazyLoaded series() {
        return series;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String foreignAuthorId() {
        return foreignAuthorId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Author author)) return false;
        return id == author.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", authorMetadataId=" + authorMetadataId +
                ", cleanName='" + cleanName + '\'' +
                ", monitored=" + monitored +
                ", monitorNewItems=" + monitorNewItems +
                ", lastInfoSync=" + lastInfoSync +
                ", path=" + path +
                ", rootFolderPath=" + rootFolderPath +
                ", added=" + added +
                ", qualityProfileId=" + qualityProfileId +
                ", metadataProfileId=" + metadataProfileId +
                ", tags=" + tags +
                ", addOptions=" + addOptions +
                ", metadata=" + metadata +
                ", qualityProfile=" + qualityProfile +
                ", metadataProfile=" + metadataProfile +
                ", books=" + books +
                ", series=" + series +
                ", name='" + name + '\'' +
                ", foreignAuthorId='" + foreignAuthorId + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
