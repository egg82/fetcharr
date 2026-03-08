package me.egg82.fetcharr.web.model.sonarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.BooleanParser;
import me.egg82.fetcharr.parse.InstantParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import me.egg82.fetcharr.web.model.common.MediaCover;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Episode extends AbstractAPIObject<Episode> {
    public static Episode UNKNOWN = new Episode(ArrAPI.UNKNOWN, -1);

    private final int id;

    private int absoluteEpisodeNumber;
    private Instant airDate;
    private Instant airDateUtc;
    private Instant endTime;
    private EpisodeFile episodeFile;
    private int episodeNumber;
    private String finaleType;
    private Instant grabDate;
    private boolean hasFile;
    private final Set<@NotNull MediaCover> images = new HashSet<>();
    private Instant lastSearchTime;
    private boolean monitored;
    private String overview;
    private Duration runtime;
    private int sceneAbsoluteEpisodeNumber;
    private int sceneEpisodeNumber;
    private int sceneSeasonNumber;
    private int seasonNumber;
    private Series series;
    private String title;
    private int tvdbId;
    private boolean unverifiedSceneNumbering;

    public Episode(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/episode/" + id);
        this.id = id;
    }

    @Override
    public Episode fetch(@NotNull String apiKey) {
        if (this.id < 0 || !this.fetching.compareAndSet(false, true)) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile(id));
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile(id);
            try {
                parse(data.read());
                if (this.title != null && !this.title.isBlank()) {
                    this.fetched = meta.fetched();
                    this.fetching.set(false);
                    return this;
                }
            } catch (Exception ex) {
                logger.warn("Could not read data from {}", data.path(), ex);
            }
        }

        JsonNode node = get(apiKey);
        if (node == null) {
            logger.warn("Could not read data from {}", url());
            // Not setting fetched = invalid
            this.fetching.set(false);
            return this;
        }

        try {
            parse(node);
        } catch (Exception ex) {
            logger.warn("Could not read data from {}", url(), ex);
            this.fetching.set(false);
            return this;
        }

        this.fetched = Instant.now();
        try {
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
        this.fetching.set(false);
        return this;
    }

    @Override
    public boolean valid() {
        if (this.fetched == null) {
            return false;
        }

        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        if (!useCache) {
            return false;
        }

        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);
        return this.fetched.plus(cacheTime.duration()).isAfter(Instant.now());
    }

    @Override
    public boolean unknown() {
        return this.id < 0;
    }

    @Override
    public void invalidate() {
        try {
            cacheFile(id).delete();
            metaFile(id).delete();
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}-{}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), id, ex);
        }
    }

    @Override
    protected void parse(@NotNull JsonNode data) {
        JSONObject obj = data.getObject();

        if (obj == null || obj.isEmpty()) {
            return;
        }

        this.absoluteEpisodeNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "absoluteEpisodeNumber"));
        this.airDate = InstantParser.parse(StringParser.parse(obj, "airDate"));
        this.airDateUtc = InstantParser.parse(StringParser.parse(obj, "airDateUtc"));
        this.endTime = InstantParser.parse(StringParser.parse(obj, "endTime"));

        int id = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeFileId"));
        this.episodeFile = id >= 0 ? api.fetch(EpisodeFile.class, id) : EpisodeFile.UNKNOWN;

        this.episodeNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "episodeNumber"));
        this.finaleType = StringParser.parse(obj, "finaleType");
        this.grabDate = InstantParser.parse(StringParser.parse(obj, "grabDate"));
        this.hasFile = BooleanParser.parse(false, StringParser.parse(obj, "hasFile"));

        this.images.clear();
        JSONArray images = obj.has("images") ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(images.getJSONObject(i)));
            }
        }

        this.lastSearchTime = InstantParser.parse(StringParser.parse(obj, "lastSearchTime"));
        this.monitored = BooleanParser.parse(false, StringParser.parse(obj, "monitored"));
        this.overview = StringParser.parse(obj, "overview");
        this.runtime = Duration.ofSeconds(NumberParser.parseInt(-1, StringParser.parse(obj, "runtime")));
        this.sceneAbsoluteEpisodeNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "sceneAbsoluteEpisodeNumber"));
        this.sceneEpisodeNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "sceneEpisodeNumber"));
        this.sceneSeasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "sceneSeasonNumber"));
        this.seasonNumber = NumberParser.parseInt(-1, StringParser.parse(obj, "seasonNumber"));

        id = NumberParser.parseInt(-1, StringParser.parse(obj, "seriesId"));
        this.series = id >= 0 ? api.fetch(Series.class, id) : Series.UNKNOWN;

        this.tvdbId = NumberParser.parseInt(-1, StringParser.parse(obj, "tvdbId"));
        this.unverifiedSceneNumbering = BooleanParser.parse(false, StringParser.parse(obj, "unverifiedSceneNumbering"));

        this.title = StringParser.parse(obj, "title");
    }

    public int id() {
        return id;
    }

    public int absoluteEpisodeNumber() {
        return absoluteEpisodeNumber;
    }

    public @Nullable Instant airDate() {
        return airDate;
    }

    public @Nullable Instant airDateUtc() {
        return airDateUtc;
    }

    public @Nullable Instant endTime() {
        return endTime;
    }

    public @NotNull EpisodeFile episodeFile() {
        return episodeFile;
    }

    public int episodeNumber() {
        return episodeNumber;
    }

    public @Nullable String finaleType() {
        return finaleType;
    }

    public @Nullable Instant grabDate() {
        return grabDate;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public @NotNull Set<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable Instant lastSearchTime() {
        return lastSearchTime;
    }

    public boolean monitored() {
        return monitored;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @NotNull Duration runtime() {
        return runtime;
    }

    public int sceneAbsoluteEpisodeNumber() {
        return sceneAbsoluteEpisodeNumber;
    }

    public int sceneEpisodeNumber() {
        return sceneEpisodeNumber;
    }

    public int sceneSeasonNumber() {
        return sceneSeasonNumber;
    }

    public int seasonNumber() {
        return seasonNumber;
    }

    public @NotNull Series series() {
        return series;
    }

    public @Nullable String title() {
        return title;
    }

    public int tvdbId() {
        return tvdbId;
    }

    public boolean unverifiedSceneNumbering() {
        return unverifiedSceneNumbering;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Episode episode)) return false;
        return id == episode.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id=" + id +
                ", absoluteEpisodeNumber=" + absoluteEpisodeNumber +
                ", airDate=" + airDate +
                ", airDateUtc=" + airDateUtc +
                ", endTime=" + endTime +
                ", episodeFile=" + episodeFile +
                ", episodeNumber=" + episodeNumber +
                ", finaleType='" + finaleType + '\'' +
                ", grabDate=" + grabDate +
                ", hasFile=" + hasFile +
                ", images=" + images +
                ", lastSearchTime=" + lastSearchTime +
                ", monitored=" + monitored +
                ", overview='" + overview + '\'' +
                ", runtime=" + runtime +
                ", sceneAbsoluteEpisodeNumber=" + sceneAbsoluteEpisodeNumber +
                ", sceneEpisodeNumber=" + sceneEpisodeNumber +
                ", sceneSeasonNumber=" + sceneSeasonNumber +
                ", seasonNumber=" + seasonNumber +
                ", series=" + series +
                ", title='" + title + '\'' +
                ", tvdbId=" + tvdbId +
                ", unverifiedSceneNumbering=" + unverifiedSceneNumbering +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }
}
