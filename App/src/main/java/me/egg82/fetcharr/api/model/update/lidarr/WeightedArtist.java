package me.egg82.fetcharr.api.model.update.lidarr;

import me.egg82.arr.lidarr.v1.schema.AlbumResource;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.fetcharr.util.Weighted;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class WeightedArtist implements Weighted {
    private final ArtistResource artist;
    private final List<@NotNull AlbumResource> albums;
    private final Instant latest;

    private Instant lastSelected = Instant.EPOCH;

    public WeightedArtist(@NotNull ArtistResource artist, @NotNull List<@NotNull AlbumResource> albums) {
        this.artist = artist;
        this.albums = albums;

        Instant latest = Instant.EPOCH;
        for (AlbumResource a : albums) {
            Instant t = a.lastSearchTime();
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

    public @NotNull ArtistResource artist() {
        return artist;
    }

    public @NotNull List<@NotNull AlbumResource> albums() {
        return albums;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeightedArtist that)) return false;
        return Objects.equals(artist, that.artist) && Objects.equals(albums, that.albums) && Objects.equals(latest, that.latest) && Objects.equals(lastSelected, that.lastSelected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artist, albums, latest, lastSelected);
    }

    @Override
    public String toString() {
        return "WeightedArtist{" +
                "artist=" + artist +
                ", albums=" + albums +
                ", latest=" + latest +
                ", lastSelected=" + lastSelected +
                '}';
    }
}
