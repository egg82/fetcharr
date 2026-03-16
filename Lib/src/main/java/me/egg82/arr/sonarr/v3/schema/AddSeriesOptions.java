package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddSeriesOptions extends AbstractAPIObject {
    private final boolean ignoreEpisodesWithFiles;
    private final boolean ignoreEpisodesWithoutFiles;
    private final MonitorTypes monitor;
    private final boolean searchForCutoffUnmetEpisodes;
    private final boolean searchForMissingEpisodes;

    public AddSeriesOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.ignoreEpisodesWithFiles = BooleanParser.get(false, obj, "ignoreEpisodesWithFiles");
        this.ignoreEpisodesWithoutFiles = BooleanParser.get(false, obj, "ignoreEpisodesWithoutFiles");
        this.monitor = MonitorTypes.get(MonitorTypes.UNKNOWN, obj, "monitor");
        this.searchForCutoffUnmetEpisodes = BooleanParser.get(false, obj, "searchForCutoffUnmetEpisodes");
        this.searchForMissingEpisodes = BooleanParser.get(false, obj, "searchForMissingEpisodes");
    }

    public boolean isIgnoreEpisodesWithFiles() {
        return ignoreEpisodesWithFiles;
    }

    public boolean isIgnoreEpisodesWithoutFiles() {
        return ignoreEpisodesWithoutFiles;
    }

    public @NotNull MonitorTypes getMonitor() {
        return monitor;
    }

    public boolean isSearchForCutoffUnmetEpisodes() {
        return searchForCutoffUnmetEpisodes;
    }

    public boolean isSearchForMissingEpisodes() {
        return searchForMissingEpisodes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddSeriesOptions that)) return false;
        return ignoreEpisodesWithFiles == that.ignoreEpisodesWithFiles && ignoreEpisodesWithoutFiles == that.ignoreEpisodesWithoutFiles && searchForCutoffUnmetEpisodes == that.searchForCutoffUnmetEpisodes && searchForMissingEpisodes == that.searchForMissingEpisodes && monitor == that.monitor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoreEpisodesWithFiles, ignoreEpisodesWithoutFiles, monitor, searchForCutoffUnmetEpisodes, searchForMissingEpisodes);
    }

    @Override
    public String toString() {
        return "AddSeriesOptions{" +
                "ignoreEpisodesWithFiles=" + ignoreEpisodesWithFiles +
                ", ignoreEpisodesWithoutFiles=" + ignoreEpisodesWithoutFiles +
                ", monitor=" + monitor +
                ", searchForCutoffUnmetEpisodes=" + searchForCutoffUnmetEpisodes +
                ", searchForMissingEpisodes=" + searchForMissingEpisodes +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
