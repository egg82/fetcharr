package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddMovieOptions extends AbstractAPIObject {
    private final boolean ignoreEpisodesWithFiles;
    private final boolean ignoreEpisodesWithoutFiles;
    private final MonitorTypes monitor;
    private final boolean searchForMovie;
    private final AddMovieMethod addMethod;

    public AddMovieOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.ignoreEpisodesWithFiles = BooleanParser.get(false, obj, "ignoreEpisodesWithFiles");
        this.ignoreEpisodesWithoutFiles = BooleanParser.get(false, obj, "ignoreEpisodesWithoutFiles");
        this.monitor = MonitorTypes.get(MonitorTypes.NONE, obj, "monitor");
        this.searchForMovie = BooleanParser.get(false, obj, "searchForMovie");
        this.addMethod = AddMovieMethod.get(AddMovieMethod.MANUAL, obj, "addMethod");
    }

    public boolean ignoreEpisodesWithFiles() {
        return ignoreEpisodesWithFiles;
    }

    public boolean ignoreEpisodesWithoutFiles() {
        return ignoreEpisodesWithoutFiles;
    }

    public @NotNull MonitorTypes monitor() {
        return monitor;
    }

    public boolean searchForMovie() {
        return searchForMovie;
    }

    public @NotNull AddMovieMethod addMethod() {
        return addMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddMovieOptions that)) return false;
        return ignoreEpisodesWithFiles == that.ignoreEpisodesWithFiles && ignoreEpisodesWithoutFiles == that.ignoreEpisodesWithoutFiles && searchForMovie == that.searchForMovie && monitor == that.monitor && addMethod == that.addMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoreEpisodesWithFiles, ignoreEpisodesWithoutFiles, monitor, searchForMovie, addMethod);
    }

    @Override
    public String toString() {
        return "AddMovieOptions{" +
                "ignoreEpisodesWithFiles=" + ignoreEpisodesWithFiles +
                ", ignoreEpisodesWithoutFiles=" + ignoreEpisodesWithoutFiles +
                ", monitor=" + monitor +
                ", searchForMovie=" + searchForMovie +
                ", addMethod=" + addMethod +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
