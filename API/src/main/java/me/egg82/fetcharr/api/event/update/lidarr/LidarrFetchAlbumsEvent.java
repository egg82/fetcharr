package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.Album;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Album} is fetched from the API for updating.
 */
public class LidarrFetchAlbumsEvent extends AbstractUpdaterEvent {
    private final Album albums;
    private final ArtistResource artist;

    public LidarrFetchAlbumsEvent(@NotNull Album albums, @NotNull ArtistResource artist, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.albums = albums;
        this.artist = artist;
    }

    public @NotNull Album albums() {
        return albums;
    }

    public @NotNull ArtistResource artist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrFetchAlbumsEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(albums, that.albums) && Objects.equals(artist, that.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), albums, artist);
    }

    @Override
    public String toString() {
        return "LidarrFetchAlbumsEvent{" +
                "albums=" + albums +
                ", artist=" + artist +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
