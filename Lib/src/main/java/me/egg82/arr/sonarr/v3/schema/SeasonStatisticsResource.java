package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.InstantParser;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SeasonStatisticsResource extends AbstractAPIObject {
    private final int episodeCount;
    private final int episodeFileCount;
    private final Instant nextAiring;
    private final float percentOfEpisodes;
    private final Instant previousAiring;
    private final Set<@NotNull String> releaseGroups = new HashSet<>();
    private final long sizeOnDisk;
    private final int totalEpisodeCount;

    public SeasonStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.episodeCount = NumberParser.getInt(-1, obj, "episodeCount");
        this.episodeFileCount = NumberParser.getInt(-1, obj, "episodeFileCount");
        this.nextAiring = InstantParser.get(obj, "nextAiring");
        this.percentOfEpisodes = NumberParser.getFloat(-1.0F, obj, "percentOfEpisodes");
        this.previousAiring = InstantParser.get(obj, "previousAiring");

        JSONArray releaseGroups = obj.has("releaseGroups") && obj.get("releaseGroups") != null ? obj.getJSONArray("releaseGroups") : null;
        if (releaseGroups != null) {
            for (int i = 0; i < releaseGroups.length(); i++) {
                this.releaseGroups.add(releaseGroups.getString(i));
            }
        }

        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");
        this.totalEpisodeCount = NumberParser.getInt(-1, obj, "totalEpisodeCount");
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public int getEpisodeFileCount() {
        return episodeFileCount;
    }

    public @Nullable Instant getNextAiring() {
        return nextAiring;
    }

    public float getPercentOfEpisodes() {
        return percentOfEpisodes;
    }

    public @Nullable Instant getPreviousAiring() {
        return previousAiring;
    }

    public @NotNull Set<@NotNull String> getReleaseGroups() {
        return releaseGroups;
    }

    public long getSizeOnDisk() {
        return sizeOnDisk;
    }

    public int getTotalEpisodeCount() {
        return totalEpisodeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeasonStatisticsResource that)) return false;
        return episodeCount == that.episodeCount && episodeFileCount == that.episodeFileCount && Float.compare(percentOfEpisodes, that.percentOfEpisodes) == 0 && sizeOnDisk == that.sizeOnDisk && totalEpisodeCount == that.totalEpisodeCount && Objects.equals(nextAiring, that.nextAiring) && Objects.equals(previousAiring, that.previousAiring) && Objects.equals(releaseGroups, that.releaseGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episodeCount, episodeFileCount, nextAiring, percentOfEpisodes, previousAiring, releaseGroups, sizeOnDisk, totalEpisodeCount);
    }

    @Override
    public String toString() {
        return "SeasonStatisticsResource{" +
                "episodeCount=" + episodeCount +
                ", episodeFileCount=" + episodeFileCount +
                ", nextAiring=" + nextAiring +
                ", percentOfEpisodes=" + percentOfEpisodes +
                ", previousAiring=" + previousAiring +
                ", releaseGroups=" + releaseGroups +
                ", sizeOnDisk=" + sizeOnDisk +
                ", totalEpisodeCount=" + totalEpisodeCount +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
