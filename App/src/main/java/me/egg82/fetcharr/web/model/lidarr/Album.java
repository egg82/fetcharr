package me.egg82.fetcharr.web.model.lidarr;

import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import me.egg82.fetcharr.web.model.common.Link;
import me.egg82.fetcharr.web.model.common.MediaCover;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Album extends AbstractAPIObject<Album> {
    public static Album UNKNOWN = new Album(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String title;
    private String disambiguation;
    private String overview;
    private Artist artist;
    private String foreignAlbumId;
    private boolean monitored;
    private boolean anyReleaseOk;
    private Profile profile;
    private Duration duration;
    private String albumType;
    private final Set<@NotNull String> secondaryTypes = new HashSet<>();
    private int mediumCount;
    private Ratings ratings;
    private Instant releaseDate;
    private final Set<@NotNull Release> releases = new HashSet<>();
    private final Set<@NotNull String> genres = new HashSet<>();
    private final Set<@NotNull Medium> media = new HashSet<>();
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private final Set<@NotNull Link> links = new HashSet<>();
    private Instant lastSearchTime;
    private Statistics statistics;
    private AddOptions addOptions;
    private String remoteCover;

    public Album(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/album/" + id);
        this.id = id;
    }
}
