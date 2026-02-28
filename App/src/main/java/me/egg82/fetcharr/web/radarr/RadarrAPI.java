package me.egg82.fetcharr.web.radarr;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.ParsedTime;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.common.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RadarrAPI extends CommonAPI {
    private final LoadingCache<Integer, @Nullable Movie> movies;
    private final LoadingCache<Integer, @Nullable MovieFile> movieFiles;
    private final LoadingCache<Integer, @Nullable QualityProfile> qualityProfiles;
    private final LoadingCache<Integer, @Nullable CustomFormat> customFormats;
    private final LoadingCache<Integer, @Nullable Language> languages;

    private final boolean useCache = ConfigVars.getVar(ConfigVars.USE_CACHE, (boolean) ConfigVars.USE_CACHE.def());

    private final ParsedTime shortCacheTime;
    private final ParsedTime longCacheTime;

    public RadarrAPI(@NotNull String url, @NotNull String key, int id) {
        super(url, key, id);

        this.shortCacheTime = ConfigVars.getVar(ConfigVars.SHORT_CACHE_TIME, (ParsedTime) ConfigVars.SHORT_CACHE_TIME.def());
        this.longCacheTime = ConfigVars.getVar(ConfigVars.LONG_CACHE_TIME, (ParsedTime) ConfigVars.LONG_CACHE_TIME.def());

        this.movies = Caffeine.newBuilder()
                .expireAfterWrite(shortCacheTime.time(), shortCacheTime.unit())
                .build(this::movieInternal);
        this.movieFiles = Caffeine.newBuilder()
                .expireAfterWrite(shortCacheTime.time(), shortCacheTime.unit())
                .build(this::movieFileInternal);

        this.qualityProfiles = Caffeine.newBuilder()
                .expireAfterWrite(longCacheTime.time(), longCacheTime.unit())
                .build(this::qualityProfileInternal);
        this.customFormats = Caffeine.newBuilder()
                .expireAfterWrite(longCacheTime.time(), longCacheTime.unit())
                .build(this::customFormatInternal);
        this.languages = Caffeine.newBuilder()
                .expireAfterWrite(longCacheTime.time(), longCacheTime.unit())
                .build(this::languageInternal);
    }

    @Override
    public boolean valid() {
        logger.debug("Getting API response for {}", url);

        JsonNode response = get("/api");
        if (response == null) {
            logger.warn("Radarr instance is invalid for URL {}", url + "/api");
            return false;
        }
        if (!response.getObject().get("current").equals("v3")) {
            logger.warn("Radarr is not expected API version (got {}) for URL {}", response.getObject().get("current"), url + "/api");
            return false;
        }
        return true;
    }

    @Override
    public @Nullable QualityProfile qualityProfile(int id, boolean cache) {
        if (!cache) {
            qualityProfiles.invalidate(id);
        }
        return useCache ? qualityProfiles.get(id) : qualityProfileInternal(id);
    }

    private @Nullable QualityProfile qualityProfileInternal(int id) {
        JSONFile file = new JSONFile(APIObject.getPath(this, QualityProfile.class, id));
        JSONFile metaFile = new JSONFile(APIObject.getMetaPath(this, QualityProfile.class, id));

        if (file.exists() && metaFile.exists()) {
            try {
                APIMeta meta = new APIMeta(metaFile.read());
                LocalDateTime expiration = meta.created().plusNanos(longCacheTime.unit().toNanos(longCacheTime.time()));
                if (expiration.isAfter(LocalDateTime.now())) {
                    logger.debug("Getting quality profile at ID {}: {}", id, file.path());
                    return new QualityProfile(file.read(), this);
                } else {
                    metaFile.delete();
                    file.delete();
                }
            } catch (IOException ex) {
                logger.warn("Could not open file at {}", file.path(), ex);
            }
        }

        logger.debug("Getting quality profile at ID {}: {}", id, url + "/api/v3/qualityprofile/" + id);

        JsonNode response = get("/api/v3/qualityprofile/" + id);
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/qualityprofile/" + id);
            return null;
        }

        try {
            int status = response.getObject().getInt("status");
            logger.warn("Radarr instance returned status code {} for URL {}", status, url + "/api/v3/qualityprofile/" + id);
            return null;
        } catch (JSONException ignored) {
            return new QualityProfile(response.getObject(), this);
        }
    }

    @Override
    public @Nullable CustomFormat customFormat(int id, boolean cache) {
        if (!cache) {
            customFormats.invalidate(id);
        }
        return useCache ? customFormats.get(id) : customFormatInternal(id);
    }

    public @Nullable CustomFormat customFormatInternal(int id) {
        JSONFile file = new JSONFile(APIObject.getPath(this, CustomFormat.class, id));
        JSONFile metaFile = new JSONFile(APIObject.getMetaPath(this, CustomFormat.class, id));

        if (file.exists() && metaFile.exists()) {
            try {
                APIMeta meta = new APIMeta(metaFile.read());
                LocalDateTime expiration = meta.created().plusNanos(longCacheTime.unit().toNanos(longCacheTime.time()));
                if (expiration.isAfter(LocalDateTime.now())) {
                    logger.debug("Getting custom format at ID {}: {}", id, file.path());
                    return new CustomFormat(file.read(), this);
                } else {
                    metaFile.delete();
                    file.delete();
                }
            } catch (IOException ex) {
                logger.warn("Could not open file at {}", file.path(), ex);
            }
        }

        logger.debug("Getting custom format at ID {}: {}", id, url + "/api/v3/customformat/" + id);

        JsonNode response = get("/api/v3/customformat/" + id);
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/customformat/" + id);
            return null;
        }

        try {
            int status = response.getObject().getInt("status");
            logger.warn("Radarr instance returned status code {} for URL {}", status, url + "/api/v3/customformat/" + id);
            return null;
        } catch (JSONException ignored) {
            return new CustomFormat(response.getObject(), this);
        }
    }

    @Override
    public @Nullable Language language(int id, boolean cache) {
        if (!cache) {
            languages.invalidate(id);
        }
        return useCache ? languages.get(id) : languageInternal(id);
    }

    public @Nullable Language languageInternal(int id) {
        JSONFile file = new JSONFile(APIObject.getPath(this, Language.class, id));
        JSONFile metaFile = new JSONFile(APIObject.getMetaPath(this, Language.class, id));

        if (file.exists() && metaFile.exists()) {
            try {
                APIMeta meta = new APIMeta(metaFile.read());
                LocalDateTime expiration = meta.created().plusNanos(longCacheTime.unit().toNanos(longCacheTime.time()));
                if (expiration.isAfter(LocalDateTime.now())) {
                    logger.debug("Getting language at ID {}: {}", id, file.path());
                    return new Language(file.read(), this);
                } else {
                    metaFile.delete();
                    file.delete();
                }
            } catch (IOException ex) {
                logger.warn("Could not open file at {}", file.path(), ex);
            }
        }

        logger.debug("Getting language at ID {}: {}", id, url + "/api/v3/language/" + id);

        JsonNode response = get("/api/v3/language/" + id);
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/language/" + id);
            return null;
        }

        try {
            int status = response.getObject().getInt("status");
            logger.warn("Radarr instance returned status code {} for URL {}", status, url + "/api/v3/language/" + id);
            return null;
        } catch (JSONException ignored) {
            return new Language(response.getObject(), this);
        }
    }

    public @NotNull Set<@NotNull Movie> movies() {
        return movies(true);
    }

    public @NotNull Set<@NotNull Movie> movies(boolean cache) {
        logger.debug("Getting all movies: {}", url + "/api/v3/movie");

        if (!cache) {
            movies.invalidateAll();
        }

        JsonNode response = get("/api/v3/movie");
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/movie");
            return Set.of();
        }

        Set<@NotNull Movie> val = new HashSet<>();
        JSONArray arr = response.getArray();
        for (int i = 0; i < arr.length(); i++) {
            try {
                Movie m = useCache ? movies.get(arr.getJSONObject(i).getInt("id")) : movieInternal(arr.getJSONObject(i).getInt("id"));
                if (m != null) {
                    val.add(m);
                }
            } catch (JSONException ex) {
                logger.warn("Could not transform id at {} to int", i, ex);
            }
        }
        return val;
    }

    public @Nullable Movie movie(int id) { return movie(id, true); }

    public @Nullable Movie movie(int id, boolean cache) {
        if (!cache) {
            movies.invalidate(id);
        }
        return useCache ? movies.get(id) : movieInternal(id);
    }

    private @Nullable Movie movieInternal(int id) {
        JSONFile file = new JSONFile(APIObject.getPath(this, Movie.class, id));
        JSONFile metaFile = new JSONFile(APIObject.getMetaPath(this, Movie.class, id));

        if (file.exists() && metaFile.exists()) {
            try {
                APIMeta meta = new APIMeta(metaFile.read());
                LocalDateTime expiration = meta.created().plusNanos(shortCacheTime.unit().toNanos(shortCacheTime.time()));
                if (expiration.isAfter(LocalDateTime.now())) {
                    logger.debug("Getting movie at ID {}: {}", id, file.path());
                    return new Movie(file.read(), this);
                } else {
                    metaFile.delete();
                    file.delete();
                }
            } catch (IOException ex) {
                logger.warn("Could not open file at {}", file.path(), ex);
            }
        }

        logger.debug("Getting movie at ID {}: {}", id, url + "/api/v3/movie/" + id);

        JsonNode response = get("/api/v3/movie/" + id);
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/movie/" + id);
            return null;
        }

        try {
            int status = response.getObject().getInt("status");
            logger.warn("Radarr instance returned status code {} for URL {}", status, url + "/api/v3/movie/" + id);
            return null;
        } catch (JSONException ignored) {
            return new Movie(response.getObject(), this);
        }
    }

    public @Nullable MovieFile movieFile(int id) { return movieFiles.get(id); }

    public @Nullable MovieFile movieFileInternal(int id) {
        JSONFile file = new JSONFile(APIObject.getPath(this, MovieFile.class, id));
        JSONFile metaFile = new JSONFile(APIObject.getMetaPath(this, MovieFile.class, id));

        if (file.exists() && metaFile.exists()) {
            try {
                APIMeta meta = new APIMeta(metaFile.read());
                LocalDateTime expiration = meta.created().plusNanos(shortCacheTime.unit().toNanos(shortCacheTime.time()));
                if (expiration.isAfter(LocalDateTime.now())) {
                    logger.debug("Getting movie file at ID {}: {}", id, file.path());
                    return new MovieFile(file.read(), this);
                } else {
                    metaFile.delete();
                    file.delete();
                }
            } catch (IOException ex) {
                logger.warn("Could not open file at {}", file.path(), ex);
            }
        }

        logger.debug("Getting movie file at ID {}: {}", id, url + "/api/v3/moviefile/" + id);

        JsonNode response = get("/api/v3/moviefile/" + id);
        if (response == null) {
            logger.warn("Radarr instance returned invalid response for URL {}", url + "/api/v3/moviefile/" + id);
            return null;
        }

        try {
            int status = response.getObject().getInt("status");
            logger.warn("Radarr instance returned status code {} for URL {}", status, url + "/api/v3/moviefile/" + id);
            return null;
        } catch (JSONException ignored) {
            return new MovieFile(response.getObject(), this);
        }
    }
}
