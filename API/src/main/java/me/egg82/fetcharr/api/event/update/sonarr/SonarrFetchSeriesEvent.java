package me.egg82.fetcharr.api.event.update.sonarr;

import me.egg82.arr.sonarr.v3.Series;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Series} is fetched from the API for updating.
 */
public class SonarrFetchSeriesEvent extends AbstractUpdaterEvent {
    private final Series series;

    public SonarrFetchSeriesEvent(@NotNull Series series, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.series = series;
    }

    public @NotNull Series series() {
        return series;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarrFetchSeriesEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(series, that.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), series);
    }

    @Override
    public String toString() {
        return "SonarrFetchSeriesEvent{" +
                "series=" + series +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
