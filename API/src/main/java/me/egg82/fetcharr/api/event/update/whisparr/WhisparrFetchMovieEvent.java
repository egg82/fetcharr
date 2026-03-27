package me.egg82.fetcharr.api.event.update.whisparr;

import me.egg82.arr.whisparr.v3.Movie;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Movie} is fetched from the API for updating.
 */
public class WhisparrFetchMovieEvent extends AbstractUpdaterEvent {
    private final Movie movie;

    public WhisparrFetchMovieEvent(@NotNull Movie movie, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.movie = movie;
    }

    public @NotNull Movie movie() {
        return movie;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WhisparrFetchMovieEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(movie, that.movie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), movie);
    }

    @Override
    public String toString() {
        return "WhisparrFetchMovieEvent{" +
                "movie=" + movie +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
