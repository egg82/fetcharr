package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AuthorStatisticsResource extends AbstractAPIObject {
    private final int bookFileCount;
    private final int bookCount;
    private final int availableBookCount;
    private final int totalBookCount;
    private final long sizeOnDisk;
    private final float percentOfBooks;

    public AuthorStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.bookFileCount = NumberParser.getInt(-1, obj, "bookFileCount");
        this.bookCount = NumberParser.getInt(-1, obj, "bookCount");
        this.availableBookCount = NumberParser.getInt(-1, obj, "availableBookCount");
        this.totalBookCount = NumberParser.getInt(-1, obj, "totalBookCount");
        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");
        this.percentOfBooks = NumberParser.getFloat(-1.0F, obj, "percentOfBooks");
    }

    public int bookFileCount() {
        return bookFileCount;
    }

    public int bookCount() {
        return bookCount;
    }

    public int availableBookCount() {
        return availableBookCount;
    }

    public int totalBookCount() {
        return totalBookCount;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public float percentOfBooks() {
        return percentOfBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthorStatisticsResource that)) return false;
        return bookFileCount == that.bookFileCount && bookCount == that.bookCount && availableBookCount == that.availableBookCount && totalBookCount == that.totalBookCount && sizeOnDisk == that.sizeOnDisk && Float.compare(percentOfBooks, that.percentOfBooks) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookFileCount, bookCount, availableBookCount, totalBookCount, sizeOnDisk, percentOfBooks);
    }

    @Override
    public String toString() {
        return "AuthorStatisticsResource{" +
                "bookFileCount=" + bookFileCount +
                ", bookCount=" + bookCount +
                ", availableBookCount=" + availableBookCount +
                ", totalBookCount=" + totalBookCount +
                ", sizeOnDisk=" + sizeOnDisk +
                ", percentOfBooks=" + percentOfBooks +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
