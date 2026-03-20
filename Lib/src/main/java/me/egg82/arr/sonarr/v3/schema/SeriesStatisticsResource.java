package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;
import org.pcollections.TreePSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SeriesStatisticsResource extends AbstractAPIObject {
    private final int episodeCount;
    private final int episodeFileCount;
    private final float percentOfEpisodes;
    private final PSet<@NotNull String> releaseGroups;
    private final int seasonCount;
    private final long sizeOnDisk;
    private final int totalEpisodeCount;

    public SeriesStatisticsResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.episodeCount = NumberParser.getInt(-1, obj, "episodeCount");
        this.episodeFileCount = NumberParser.getInt(-1, obj, "episodeFileCount");
        this.percentOfEpisodes = NumberParser.getFloat(-1.0F, obj, "percentOfEpisodes");

        JSONArray releaseGroups = obj.has("releaseGroups") && obj.get("releaseGroups") != null ? obj.getJSONArray("releaseGroups") : null;
        Set<@NotNull String> releaseGroupsL = new HashSet<>();
        if (releaseGroups != null) {
            for (int i = 0; i < releaseGroups.length(); i++) {
                releaseGroupsL.add(releaseGroups.getString(i));
            }
        }
        this.releaseGroups = TreePSet.from(releaseGroupsL);

        this.seasonCount = NumberParser.getInt(-1, obj, "seasonCount");
        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");
        this.totalEpisodeCount = NumberParser.getInt(-1, obj, "totalEpisodeCount");
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public int getEpisodeFileCount() {
        return episodeFileCount;
    }

    public float getPercentOfEpisodes() {
        return percentOfEpisodes;
    }

    public @NotNull PSet<@NotNull String> getReleaseGroups() {
        return releaseGroups;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public long getSizeOnDisk() {
        return sizeOnDisk;
    }

    public int getTotalEpisodeCount() {
        return totalEpisodeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeriesStatisticsResource that)) return false;
        return episodeCount == that.episodeCount && episodeFileCount == that.episodeFileCount && Float.compare(percentOfEpisodes, that.percentOfEpisodes) == 0 && seasonCount == that.seasonCount && sizeOnDisk == that.sizeOnDisk && totalEpisodeCount == that.totalEpisodeCount && Objects.equals(releaseGroups, that.releaseGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episodeCount, episodeFileCount, percentOfEpisodes, releaseGroups, seasonCount, sizeOnDisk, totalEpisodeCount);
    }

    @Override
    public String toString() {
        return "SeriesStatisticsResource{" +
                "episodeCount=" + episodeCount +
                ", episodeFileCount=" + episodeFileCount +
                ", percentOfEpisodes=" + percentOfEpisodes +
                ", releaseGroups=" + releaseGroups +
                ", seasonCount=" + seasonCount +
                ", sizeOnDisk=" + sizeOnDisk +
                ", totalEpisodeCount=" + totalEpisodeCount +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
