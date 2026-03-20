package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AddArtistOptions extends AbstractAPIObject {
    private final MonitorTypes monitor;
    private final PSet<@NotNull String> albumsToMonitor;
    private final boolean monitored;
    private final boolean searchForMissingAlbums;

    public AddArtistOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.monitor = MonitorTypes.get(MonitorTypes.UNKNOWN, obj, "monitor");

        JSONArray albumsToMonitor = obj.has("albumsToMonitor") && obj.get("albumsToMonitor") != null ? obj.getJSONArray("albumsToMonitor") : null;
        Set<@NotNull String> albumsToMonitorL = new HashSet<>();
        if (albumsToMonitor != null) {
            for (int i = 0; i < albumsToMonitor.length(); i++) {
                albumsToMonitorL.add(albumsToMonitor.getString(i));
            }
        }
        this.albumsToMonitor = HashTreePSet.from(albumsToMonitorL);

        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.searchForMissingAlbums = BooleanParser.get(false, obj, "searchForMissingAlbums");
    }

    public @NotNull MonitorTypes monitor() {
        return monitor;
    }

    public @NotNull PSet<@NotNull String> albumsToMonitor() {
        return albumsToMonitor;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean searchForMissingAlbums() {
        return searchForMissingAlbums;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddArtistOptions that)) return false;
        return monitored == that.monitored && searchForMissingAlbums == that.searchForMissingAlbums && monitor == that.monitor && Objects.equals(albumsToMonitor, that.albumsToMonitor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monitor, albumsToMonitor, monitored, searchForMissingAlbums);
    }

    @Override
    public String toString() {
        return "AddArtistOptions{" +
                "monitor=" + monitor +
                ", albumsToMonitor=" + albumsToMonitor +
                ", monitored=" + monitored +
                ", searchForMissingAlbums=" + searchForMissingAlbums +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
