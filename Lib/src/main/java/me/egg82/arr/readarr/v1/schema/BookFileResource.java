package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.Author;
import me.egg82.arr.readarr.v1.Book;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookFileResource extends AbstractAPIObject {
    private final int id;
    private final int authorId;
    private final int bookId;
    private final File path;
    private final long size;
    private final Instant dateAdded;
    private final QualityModel quality;
    private final int qualityWeight;
    private final int indexerFlags;
    private final MediaInfoResource mediaInfo;
    private final boolean qualityCutoffNotMet;
    private final PVector<@NotNull ParsedTrackInfo> audioTags;

    public BookFileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.authorId = NumberParser.getInt(-1, obj, "authorId");
        this.bookId = NumberParser.getInt(-1, obj, "bookId");
        this.path = FileParser.get(obj, "path");
        this.size = NumberParser.getLong(-1L, obj, "size");
        this.dateAdded = InstantParser.get(Instant.EPOCH, obj, "dateAdded");
        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");
        this.qualityWeight = NumberParser.getInt(-1, obj, "qualityWeight");
        this.indexerFlags = NumberParser.getInt(-1, obj, "indexerFlags");
        this.mediaInfo = ObjectParser.get(MediaInfoResource.class, api, obj, "mediaInfo");
        this.qualityCutoffNotMet = BooleanParser.get(false, obj, "qualityCutoffNotMet");

        JSONArray audioTags = obj.has("audioTags") && obj.get("audioTags") != null ? obj.getJSONArray("audioTags") : null;
        List<@NotNull ParsedTrackInfo> audioTagsL = new ArrayList<>();
        if (audioTags != null) {
            for (int i = 0; i < audioTags.length(); i++) {
                audioTagsL.add(new ParsedTrackInfo(api, audioTags.getJSONObject(i)));
            }
        }
        this.audioTags = TreePVector.from(audioTagsL);
    }

    public int id() {
        return id;
    }

    public @Nullable Author author() {
        return api.fetch(Author.class, authorId);
    }

    public @Nullable Book book() {
        return api.fetch(Book.class, bookId);
    }

    public @Nullable File path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @NotNull Instant dateAdded() {
        return dateAdded;
    }

    public @Nullable QualityModel quality() {
        return quality;
    }

    public int qualityWeight() {
        return qualityWeight;
    }

    public int indexerFlags() {
        return indexerFlags;
    }

    public @Nullable MediaInfoResource mediaInfo() {
        return mediaInfo;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    public @NotNull PVector<@NotNull ParsedTrackInfo> audioTags() {
        return audioTags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookFileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BookFileResource{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", bookId=" + bookId +
                ", path=" + path +
                ", size=" + size +
                ", dateAdded=" + dateAdded +
                ", quality=" + quality +
                ", qualityWeight=" + qualityWeight +
                ", indexerFlags=" + indexerFlags +
                ", mediaInfo=" + mediaInfo +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", audioTags=" + audioTags +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
