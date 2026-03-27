package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.Episode;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Episode} is fetched from the API for updating.
 */
public class SonarrFetchEpisodesEvent extends AbstractUpdaterEvent {
    private final Episode episodes;
    private final SeriesResource series;

    public SonarrFetchEpisodesEvent(@NotNull Episode episodes, @NotNull SeriesResource series, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.episodes = episodes;
        this.series = series;
    }

    public @NotNull Episode episodes() {
        return episodes;
    }

    public @NotNull SeriesResource series() {
        return series;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrFetchEpisodesEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(episodes, that.episodes) && Objects.equals(series, that.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), episodes, series);
    }

    @Override
    public String toString() {
        return "SonarrFetchEpisodesEvent{" +
                "episodes=" + episodes +
                ", series=" + series +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
