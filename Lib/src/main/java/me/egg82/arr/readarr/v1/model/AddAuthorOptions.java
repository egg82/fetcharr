package me.egg82.arr.readarr.v1.model;

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

public class AddAuthorOptions extends AbstractAPIObject {
    private final MonitorTypes monitor;
    private final PSet<@NotNull String> booksToMonitor;
    private final boolean monitored;
    private final boolean searchForMissingBooks;

    public AddAuthorOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.monitor = MonitorTypes.get(MonitorTypes.UNKNOWN, obj, "monitor");

        JSONArray booksToMonitor = obj.has("booksToMonitor") && obj.get("booksToMonitor") != null ? obj.getJSONArray("booksToMonitor") : null;
        Set<@NotNull String> booksToMonitorL = new HashSet<>();
        if (booksToMonitor != null) {
            for (int i = 0; i < booksToMonitor.length(); i++) {
                booksToMonitorL.add(booksToMonitor.getString(i));
            }
        }
        this.booksToMonitor = HashTreePSet.from(booksToMonitorL);

        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.searchForMissingBooks = BooleanParser.get(false, obj, "searchForMissingBooks");
    }

    public @NotNull MonitorTypes monitor() {
        return monitor;
    }

    public @NotNull PSet<@NotNull String> booksToMonitor() {
        return booksToMonitor;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean searchForMissingBooks() {
        return searchForMissingBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddAuthorOptions that)) return false;
        return monitored == that.monitored && searchForMissingBooks == that.searchForMissingBooks && monitor == that.monitor && Objects.equals(booksToMonitor, that.booksToMonitor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monitor, booksToMonitor, monitored, searchForMissingBooks);
    }

    @Override
    public String toString() {
        return "AddAuthorOptions{" +
                "monitor=" + monitor +
                ", booksToMonitor=" + booksToMonitor +
                ", monitored=" + monitored +
                ", searchForMissingBooks=" + searchForMissingBooks +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
