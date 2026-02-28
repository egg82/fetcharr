package me.egg82.fetcharr.web.radarr;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ParsedDateTime;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.NullAPI;
import me.egg82.fetcharr.web.common.APIMeta;
import me.egg82.fetcharr.web.common.APIObject;
import me.egg82.fetcharr.web.common.QualityProfile;
import me.egg82.fetcharr.web.common.ReleaseStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class Movie extends APIObject {
    public static final Movie UNKNOWN = new Movie();

    private final String title;
    private final long sizeOnDisk;
    private final ReleaseStatus status;
    private final ParsedDateTime releaseDate;
    private final ParsedDateTime cinemaRelease;
    private final ParsedDateTime physicalRelease;
    private final ParsedDateTime digitalRelease;
    private final int year;
    private final String studio;
    private final String path;
    private final QualityProfile qualityProfile;
    private final boolean hasFile;
    private final MovieFile movieFile;
    private final boolean monitored;
    private final ReleaseStatus minAvailability;
    private final boolean available;
    private final String imdb;
    private final int tmdb;
    private final String certification;
    private final Set<@NotNull String> genres;
    private final Set<@NotNull String> tags;
    private final ParsedDateTime added;
    private final float popularity;
    private final ParsedDateTime lastSearch;
    private final int id;

    private final RadarrAPI api;

    public Movie(@NotNull JSONObject obj, @NotNull RadarrAPI api) {
        super(obj, api);

        this.api = api;

        this.title = getString("", "title");
        this.sizeOnDisk = getLong(-1L, "sizeOnDisk");
        this.status = getReleaseStatus(ReleaseStatus.UNKNOWN, "status");
        this.releaseDate = getDateTime(ParsedDateTime.UNKNOWN, "releaseDate");
        this.cinemaRelease = getDateTime(ParsedDateTime.UNKNOWN, "inCinemas");
        this.physicalRelease = getDateTime(ParsedDateTime.UNKNOWN, "physicalRelease");
        this.digitalRelease = getDateTime(ParsedDateTime.UNKNOWN, "digitalRelease");
        this.year = getInt(-1, "year");
        this.studio = getString("", "studio");
        this.path = getString("", "path");
        this.qualityProfile = getQualityProfile(QualityProfile.UNKNOWN, "qualityProfileId");
        this.hasFile = getBoolean(false, "hasFile");
        this.movieFile = getMovieFile(MovieFile.UNKNOWN, "movieFileId");
        this.monitored = getBoolean(false, "monitored");
        this.minAvailability = getReleaseStatus(ReleaseStatus.UNKNOWN, "minimumAvailability");
        this.available = getBoolean(false, "isAvailable");
        this.imdb = getString("", "imdbId");
        this.tmdb = getInt(-1, "tmdbId");
        this.certification = getString("", "certification");
        this.genres = getStringSet(Set.of(), "genres");
        this.tags = getStringSet(Set.of(), "tags");
        this.added = getDateTime(ParsedDateTime.UNKNOWN, "added");
        this.popularity = getFloat(-1.0F, "popularity");
        this.lastSearch = getDateTime(ParsedDateTime.UNKNOWN, "lastSearchTime");
        this.id = getInt(-1, "id");

        this.file = new JSONFile(getPath(api, getClass(), id));
        this.metaFile = new JSONFile(getMetaPath(api, getClass(), id));
        if (!file.exists()) {
            try {
                this.file.write(obj);
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
        if (!metaFile.exists()) {
            try {
                this.metaFile.write(meta().object());
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
    }

    private Movie() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.api = null;

        this.title = "";
        this.sizeOnDisk = -1L;
        this.status = ReleaseStatus.UNKNOWN;
        this.releaseDate = ParsedDateTime.UNKNOWN;
        this.cinemaRelease = ParsedDateTime.UNKNOWN;
        this.physicalRelease = ParsedDateTime.UNKNOWN;
        this.digitalRelease = ParsedDateTime.UNKNOWN;
        this.year = -1;
        this.studio = "";
        this.path = "";
        this.qualityProfile = QualityProfile.UNKNOWN;
        this.hasFile = false;
        this.movieFile = MovieFile.UNKNOWN;
        this.monitored = false;
        this.minAvailability = ReleaseStatus.UNKNOWN;
        this.available = false;
        this.imdb = "";
        this.tmdb = -1;
        this.certification = "";
        this.genres = Set.of();
        this.tags = Set.of();
        this.added = ParsedDateTime.UNKNOWN;
        this.popularity = -1.0F;
        this.lastSearch = ParsedDateTime.UNKNOWN;
        this.id = -1;

        this.file = new JSONFile(getPath(api, getClass(), id));
        this.metaFile = new JSONFile(getMetaPath(api, getClass(), id));
        if (!file.exists()) {
            try {
                this.file.write(obj);
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
        if (!metaFile.exists()) {
            try {
                this.metaFile.write(meta().object());
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
    }

    public boolean unknown() { return id < 0; }

    protected @NotNull MovieFile getMovieFile(@NotNull MovieFile def, @NotNull String... path) {
        int val = getInt(-1, path);
        if (val <= 0) {
            return def;
        }

        MovieFile v = api.movieFile(val);
        if  (v == null) {
            logger.warn("Could not transform {} to movie file. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    @Override
    public @NotNull APIMeta meta() {
        return new APIMeta();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Movie that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String title() {
        return title;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public @NotNull ReleaseStatus status() {
        return status;
    }

    public @NotNull ParsedDateTime releaseDate() {
        return releaseDate;
    }

    public @NotNull ParsedDateTime cinemaRelease() {
        return cinemaRelease;
    }

    public @NotNull ParsedDateTime physicalRelease() {
        return physicalRelease;
    }

    public @NotNull ParsedDateTime digitalRelease() {
        return digitalRelease;
    }

    public int year() {
        return year;
    }

    public @NotNull String studio() {
        return studio;
    }

    public @NotNull String path() {
        return path;
    }

    public @NotNull QualityProfile qualityProfile() {
        return qualityProfile;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public @NotNull MovieFile movieFile() {
        return movieFile;
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull ReleaseStatus minAvailability() {
        return minAvailability;
    }

    public boolean available() {
        return available;
    }

    public @NotNull String imdb() {
        return imdb;
    }

    public int tmdb() {
        return tmdb;
    }

    public @NotNull String certification() {
        return certification;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public @NotNull Set<@NotNull String> tags() {
        return tags;
    }

    public @NotNull ParsedDateTime added() {
        return added;
    }

    public float popularity() {
        return popularity;
    }

    public @NotNull ParsedDateTime lastSearch() {
        return lastSearch;
    }

    public int id() {
        return id;
    }
}
