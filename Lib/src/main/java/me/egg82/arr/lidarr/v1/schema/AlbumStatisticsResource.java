package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AlbumStatisticsResource extends AbstractAPIObject {
    private final int trackFileCount;
    private final int trackCount;
    private final int totalTrackCount;
    private final long sizeOnDisk;
    private final float percentOfTracks;

    public AlbumStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.trackFileCount = NumberParser.getInt(-1, obj, "trackFileCount");
        this.trackCount = NumberParser.getInt(-1, obj, "trackCount");
        this.totalTrackCount = NumberParser.getInt(-1, obj, "totalTrackCount");
        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");
        this.percentOfTracks = NumberParser.getFloat(-1.0F, obj, "percentOfTracks");
    }

    public int trackFileCount() {
        return trackFileCount;
    }

    public int trackCount() {
        return trackCount;
    }

    public int totalTrackCount() {
        return totalTrackCount;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public float percentOfTracks() {
        return percentOfTracks;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlbumStatisticsResource that)) return false;
        return trackFileCount == that.trackFileCount && trackCount == that.trackCount && totalTrackCount == that.totalTrackCount && sizeOnDisk == that.sizeOnDisk && Float.compare(percentOfTracks, that.percentOfTracks) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackFileCount, trackCount, totalTrackCount, sizeOnDisk, percentOfTracks);
    }

    @Override
    public String toString() {
        return "AlbumStatisticsResource{" +
                "trackFileCount=" + trackFileCount +
                ", trackCount=" + trackCount +
                ", totalTrackCount=" + totalTrackCount +
                ", sizeOnDisk=" + sizeOnDisk +
                ", percentOfTracks=" + percentOfTracks +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
