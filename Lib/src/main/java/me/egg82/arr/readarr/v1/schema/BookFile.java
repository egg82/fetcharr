package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.readarr.v1.Edition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Instant;
import java.util.Objects;

public class BookFile extends AbstractAPIObject {
    private final int id;
    private final File path;
    private final long size;
    private final Instant modified;
    private final Instant dateAdded;
    private final File originalFilePath;
    private final String sceneName;
    private final String releaseGroup;
    private final QualityModel quality;
    private final IndexerFlags indexerFlags;
    private final MediaInfoModel mediaInfo;
    private final int editionId;
    private final int calibreId;
    private final int part;
    private final AuthorLazyLoaded author;
    private final EditionLazyLoaded edition;
    private final int partCount;

    public BookFile(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.path = FileParser.get(obj, "path");
        this.size = NumberParser.getLong(-1L, obj, "size");
        this.modified = InstantParser.get(obj, "modified");
        this.dateAdded = InstantParser.get(Instant.EPOCH, obj, "dateAdded");
        this.originalFilePath = FileParser.get(obj, "originalFilePath");
        this.sceneName = StringParser.get(obj, "sceneName");
        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");
        this.indexerFlags = IndexerFlags.get(obj, "indexerFlags");
        this.mediaInfo = ObjectParser.get(MediaInfoModel.class, api, obj, "mediaInfo");
        this.editionId = NumberParser.getInt(-1, obj, "editionId");
        this.calibreId = NumberParser.getInt(-1, obj, "calibreId");
        this.part = NumberParser.getInt(-1, obj, "part");
        this.author = ObjectParser.get(AuthorLazyLoaded.class, api, obj, "author");
        this.edition = ObjectParser.get(EditionLazyLoaded.class, api, obj, "edition");
        this.partCount = NumberParser.getInt(-1, obj, "partCount");
    }

    public int id() {
        return id;
    }

    public @Nullable File path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @Nullable Instant modified() {
        return modified;
    }

    public @NotNull Instant dateAdded() {
        return dateAdded;
    }

    public @Nullable File originalFilePath() {
        return originalFilePath;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @Nullable QualityModel quality() {
        return quality;
    }

    public @Nullable IndexerFlags indexerFlags() {
        return indexerFlags;
    }

    public @Nullable MediaInfoModel mediaInfo() {
        return mediaInfo;
    }

    public @Nullable Edition edition() {
        return api.fetch(Edition.class, editionId);
    }

    public int calibreId() {
        return calibreId;
    }

    public int part() {
        return part;
    }

    public @Nullable AuthorLazyLoaded author() {
        return author;
    }

    public @Nullable EditionLazyLoaded editionLazy() {
        return edition;
    }

    public int partCount() {
        return partCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookFile bookFile)) return false;
        return id == bookFile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BookFile{" +
                "id=" + id +
                ", path=" + path +
                ", size=" + size +
                ", modified=" + modified +
                ", dateAdded=" + dateAdded +
                ", originalFilePath=" + originalFilePath +
                ", sceneName='" + sceneName + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", quality=" + quality +
                ", indexerFlags=" + indexerFlags +
                ", mediaInfo=" + mediaInfo +
                ", editionId=" + editionId +
                ", calibreId=" + calibreId +
                ", part=" + part +
                ", author=" + author +
                ", edition=" + edition +
                ", partCount=" + partCount +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
