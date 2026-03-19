package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArtistTitleInfo extends AbstractAPIObject {
    private final String title;
    private final String titleWithoutYear;
    private final int year;

    public ArtistTitleInfo(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.title = StringParser.get(obj, "title");
        this.titleWithoutYear = StringParser.get(obj, "titleWithoutYear");
        this.year = NumberParser.getInt(-1, obj, "year");
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String titleWithoutYear() {
        return titleWithoutYear;
    }

    public int year() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArtistTitleInfo that)) return false;
        return year == that.year && Objects.equals(title, that.title) && Objects.equals(titleWithoutYear, that.titleWithoutYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, titleWithoutYear, year);
    }

    @Override
    public String toString() {
        return "ArtistTitleInfo{" +
                "title='" + title + '\'' +
                ", titleWithoutYear='" + titleWithoutYear + '\'' +
                ", year=" + year +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
