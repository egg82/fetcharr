package me.egg82.fetcharr.api.event.update.lidarr;

import me.egg82.arr.lidarr.v1.Track;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Track} is fetched from the API for updating.
 */
public class LidarrFetchTracksEvent extends AbstractUpdaterEvent {
    private final Track tracks;
    private final ArtistResource artist;

    public LidarrFetchTracksEvent(@NotNull Track tracks, @NotNull ArtistResource artist, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.tracks = tracks;
        this.artist = artist;
    }

    public @NotNull Track tracks() {
        return tracks;
    }

    public @NotNull ArtistResource artist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LidarrFetchTracksEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(tracks, that.tracks) && Objects.equals(artist, that.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tracks, artist);
    }

    @Override
    public String toString() {
        return "LidarrFetchTracksEvent{" +
                "tracks=" + tracks +
                ", artist=" + artist +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
