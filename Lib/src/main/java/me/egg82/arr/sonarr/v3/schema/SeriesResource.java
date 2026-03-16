package me.egg82.arr.sonarr.v3.schema;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeriesResource extends AbstractAPIObject {
    private final Instant added;
    private final AddSeriesOptions addOptions;
    private final Duration airTime;
    private final List<@NotNull AlternateTitleResource> alternateTitles = new ArrayList<>();
    private final String certification;
    private final String cleanTitle;
    private final boolean ended;
    private final boolean episodesChanged;
    private final Instant firstAired;
    private final File folder;
    private final Set<@NotNull String> genres = new HashSet<>();
    private final int id;
    private final List<@NotNull MediaCover> images = new ArrayList<>();
    private final String imdbId;
    private final Instant lastAired;
    private final boolean monitored;
    private final NewItemMonitorTypes monitorNewItems;
    private final String network;
    private final Instant nextAiring;
    private final Language originalLanguage;
    private final String overview;
    private final File path;
    private final Instant previousAiring;
    private final String profileName;
    private final int qualityProfileId;
    private final Ratings ratings;
    private final String remotePoster;
    private final File rootFolderPath;
    private final Duration runtime;
    private final boolean seasonFolder;
    private final List<@NotNull SeasonResource> seasons = new ArrayList<>();
    private final SeriesType seriesType;
    private final String sortTitle;
    private final SeriesStatisticsResource statistics;
    private final SeriesStatusType status;
    private final IntList tags = new IntArrayList();
    private final String title;
    private final String titleSlug;
    private final int tmdbId;
    private final int tvdbId;
    private final int tvMazeId;
    private final int tvRageId;
    private final boolean useSceneNumbering;
    private final int year;

    public SeriesResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);
    }
}
