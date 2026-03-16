package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeasonResource extends AbstractAPIObject {
    private final List<@NotNull MediaCover> images = new ArrayList<>();
    private final boolean monitored;
    private final int seasonNumber;
    private final SeasonStatisticsResource statistics;

    public SeasonResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }

        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.seasonNumber = NumberParser.getInt(-1, obj, "seasonNumber");
        this.statistics = ObjectParser.get(SeasonStatisticsResource.class, api, obj, "statistics");
    }

    public @NotNull List<@NotNull MediaCover> getImages() {
        return images;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public @Nullable SeasonStatisticsResource getStatistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SeasonResource that)) return false;
        return monitored == that.monitored && seasonNumber == that.seasonNumber && Objects.equals(images, that.images) && Objects.equals(statistics, that.statistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(images, monitored, seasonNumber, statistics);
    }

    @Override
    public String toString() {
        return "SeasonResource{" +
                "images=" + images +
                ", monitored=" + monitored +
                ", seasonNumber=" + seasonNumber +
                ", statistics=" + statistics +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
