package me.egg82.fetcharr.web.model.radarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.*;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MovieFile extends AbstractAPIObject<MovieFile> {
    public static final MovieFile UNKNOWN = new MovieFile(ArrAPI.UNKNOWN, -1);

    private final int id;

    private Movie movie;
    private File relativePath;
    private File path;
    private long size;
    private Instant dateAdded;
    private String sceneName;
    private String releaseGroup;
    private String edition;
    private final Set<@NotNull Language> languages = new HashSet<>();
    private QualityModel quality;
    private final Set<@NotNull CustomFormat> customFormats = new HashSet<>();
    private int customFormatScore;
    private int indexerFlags;
    private MediaInfo mediaInfo;
    private File originalFilePath;
    private boolean qualityCutoffNotMet;

    public MovieFile(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/moviefile/" + id);
        this.id = id;
    }

    public MovieFile(@NotNull ArrAPI api, int id, @NotNull JSONObject obj) {
        this(api, id);

        CacheMeta meta = new CacheMeta(metaFile(id));

        JsonNode node = new JsonNode(obj.toString());
        try {
            parse(node);
        } catch (Exception ex) {
            logger.warn("Could not read data from {}", obj, ex);
            return;
        }

        this.fetched = Instant.now();
        try {
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
    }

    @Override
    public MovieFile fetch(@NotNull String apiKey) {
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
                if (this.movie != null) {
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

        this.relativePath = FileParser.parse(StringParser.parse(obj, "relativePath"));
        this.path = FileParser.parse(StringParser.parse(obj, "path"));
        this.size = NumberParser.parseLong(-1L, StringParser.parse(obj, "size"));
        this.dateAdded = InstantParser.parse(Instant.EPOCH, StringParser.parse(obj, "dateAdded"));
        this.sceneName = StringParser.parse(obj, "sceneName");
        this.releaseGroup = StringParser.parse(obj, "releaseGroup");
        this.edition = StringParser.parse(obj, "edition");

        this.languages.clear();
        JSONArray languages = obj.has("languages") ? obj.getJSONArray("languages") : null;
        if (languages != null) {
            for (int i = 0; i < languages.length(); i++) {
                this.languages.add(api.fetch(Language.class, id, false));
            }
        }

        this.quality = obj.has("quality") ? new QualityModel(obj.getJSONObject("quality")) : null;

        this.customFormats.clear();
        JSONArray customFormats = obj.has("customFormats") ? obj.getJSONArray("customFormats") : null;
        if (customFormats != null) {
            for (int i = 0; i < customFormats.length(); i++) {
                int id = NumberParser.parseInt(-1, StringParser.parse(customFormats.getJSONObject(i), "id"));
                if (id >= 0) {
                    this.customFormats.add(api.fetch(CustomFormat.class, id, false));
                }
            }
        }

        this.customFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "customFormatScore"));
        this.indexerFlags = NumberParser.parseInt(-1, StringParser.parse(obj, "indexerFlags"));
        this.mediaInfo = obj.has("mediaInfo") ? new MediaInfo(obj.getJSONObject("mediaInfo")) : null;
        this.originalFilePath = FileParser.parse(StringParser.parse(obj, "originalFilePath"));
        this.qualityCutoffNotMet = BooleanParser.parse(false, StringParser.parse(obj, "qualityCutoffNotMet"));

        this.movie = api.fetch(Movie.class, NumberParser.parseInt(-1, StringParser.parse(obj, "movieId")), true);
    }

    public int id() {
        return id;
    }

    public @NotNull Movie movie() {
        return movie;
    }

    public @Nullable File relativePath() {
        return relativePath;
    }

    public @Nullable File path() {
        return path;
    }

    public long size() {
        return size;
    }

    public @Nullable Instant dateAdded() {
        return dateAdded;
    }

    public @Nullable String sceneName() {
        return sceneName;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @Nullable String edition() {
        return edition;
    }

    public @NotNull Set<@NotNull Language> languages() {
        return languages;
    }

    public @NotNull QualityModel quality() {
        return quality;
    }

    public @NotNull Set<@NotNull CustomFormat> customFormats() {
        return customFormats;
    }

    public int customFormatScore() {
        return customFormatScore;
    }

    public int indexerFlags() {
        return indexerFlags;
    }

    public @NotNull MediaInfo mediaInfo() {
        return mediaInfo;
    }

    public @Nullable File originalFilePath() {
        return originalFilePath;
    }

    public boolean qualityCutoffNotMet() {
        return qualityCutoffNotMet;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieFile movieFile)) return false;
        return id == movieFile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MovieFile{" +
                "id=" + id +
                ", movie=" + movie +
                ", relativePath=" + relativePath +
                ", path=" + path +
                ", size=" + size +
                ", dateAdded=" + dateAdded +
                ", sceneName='" + sceneName + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", edition='" + edition + '\'' +
                ", languages=" + languages +
                ", quality=" + quality +
                ", customFormats=" + customFormats +
                ", customFormatScore=" + customFormatScore +
                ", indexerFlags=" + indexerFlags +
                ", mediaInfo=" + mediaInfo +
                ", originalFilePath=" + originalFilePath +
                ", qualityCutoffNotMet=" + qualityCutoffNotMet +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }
}
