package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.Artist;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Artist} is fetched from the API for updating.
 */
public class LidarrFetchArtistEvent extends AbstractUpdaterEvent {
    private final Artist artist;

    public LidarrFetchArtistEvent(@NotNull Artist artist, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.artist = artist;
    }

    public @NotNull Artist artist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrFetchArtistEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(artist, that.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artist);
    }

    @Override
    public String toString() {
        return "LidarrFetchArtistEvent{" +
                "artist=" + artist +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
