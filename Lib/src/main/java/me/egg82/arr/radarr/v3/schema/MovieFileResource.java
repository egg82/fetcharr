package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.radarr.v3.Movie;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MovieFileResource extends AbstractAPIObject {
    private final int id;
    private final Movie movie;
    private final String relativePath;
    private final File path;
    private final long size;
    private final Instant dateAdded;
    private final String sceneName;
    private final String releaseGroup;
    private final String edition;
    private final List<@NotNull Language> languages = new ArrayList<>();
    private final QualityModel quality;
    private final List<@NotNull CustomFormatResource> customFormats = new ArrayList<>();
    private final int customFormatScore;
    private final int indexerFlags;
    private final MediaInfoResource mediaInfo;
    private final File originalFilePath;
    private final boolean qualityCutoffNotMet;

    public MovieFileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);
    }
}
