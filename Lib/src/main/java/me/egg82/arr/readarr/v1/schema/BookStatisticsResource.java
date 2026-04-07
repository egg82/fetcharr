package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BookStatisticsResource extends AbstractAPIObject {
    private final int bookFileCount;
    private final int bookCount;
    private final int totalBookCount;
    private final long sizeOnDisk;
    private final float percentOfBooks;

    public BookStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.bookFileCount = NumberParser.getInt(-1, obj, "bookFileCount");
        this.bookCount = NumberParser.getInt(-1, obj, "bookCount");
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
        if (!(o instanceof BookStatisticsResource that)) return false;
        return bookFileCount == that.bookFileCount && bookCount == that.bookCount && totalBookCount == that.totalBookCount && sizeOnDisk == that.sizeOnDisk && Float.compare(percentOfBooks, that.percentOfBooks) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookFileCount, bookCount, totalBookCount, sizeOnDisk, percentOfBooks);
    }

    @Override
    public String toString() {
        return "BookStatisticsResource{" +
                "bookFileCount=" + bookFileCount +
                ", bookCount=" + bookCount +
                ", totalBookCount=" + totalBookCount +
                ", sizeOnDisk=" + sizeOnDisk +
                ", percentOfBooks=" + percentOfBooks +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
