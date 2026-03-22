package me.egg82.fetcharr.api.model.update.sonarr;

import me.egg82.arr.sonarr.v3.schema.EpisodeResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.util.Weighted;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class WeightedSeries implements Weighted {
    private final SeriesResource series;
    private final List<@NotNull EpisodeResource> episodes;
    private final Instant latest;

    private Instant lastSelected = Instant.EPOCH;

    public WeightedSeries(@NotNull SeriesResource series, @NotNull List<@NotNull EpisodeResource> episodes) {
        this.series = series;
        this.episodes = episodes;

        Instant latest = Instant.EPOCH;
        for (EpisodeResource e : episodes) {
            Instant t = e.lastSearchTime();
            if (t != null && t.isAfter(latest)) {
                latest = t;
            }
        }
        this.latest = latest;
    }

    @Override
    public @NotNull Instant lastUpdated() {
        return this.latest;
    }

    @Override
    public @NotNull Instant lastSelected() {
        return this.lastSelected;
    }

    @Override
    public void lastSelectedNow() {
        this.lastSelected = Instant.now();
    }

    public @NotNull SeriesResource series() {
        return series;
    }

    public @NotNull List<@NotNull EpisodeResource> episodes() {
        return episodes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeightedSeries that)) return false;
        return Objects.equals(series, that.series) && Objects.equals(episodes, that.episodes) && Objects.equals(lastSelected, that.lastSelected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, episodes, lastSelected);
    }

    @Override
    public String toString() {
        return "WeightedSeries{" +
                "series=" + series +
                ", episodes=" + episodes +
                ", lastSelected=" + lastSelected +
                '}';
    }
}
