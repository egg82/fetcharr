package me.egg82.arr.radarr.v3.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.*;
import me.egg82.arr.radarr.v3.QualityProfile;
import me.egg82.arr.radarr.v3.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class MovieResource extends AbstractAPIObject {
    private final int id;
    private final String title;
    private final String originalTitle;
    private final Language originalLanguage;
    private final PVector<@NotNull AlternativeTitleResource> alternateTitles;
    private final int secondaryYear;
    private final int secondaryYearSourceId;
    private final String sortTitle;
    private final long sizeOnDisk;
    private final MovieStatusType status;
    private final String overview;
    private final Instant inCinemas;
    private final Instant physicalRelease;
    private final Instant digitalRelease;
    private final Instant releaseDate;
    private final String physicalReleaseNote;
    private final PVector<@NotNull MediaCover> images;
    private final String website;
    private final String remotePoster;
    private final int year;
    private final String youTubeTrailerId;
    private final String studio;
    private final File path;
    private final int qualityProfileId;
    private final boolean hasFile;
    private final boolean monitored;
    private final MovieStatusType minimumAvailability;
    private final boolean isAvailable;
    private final String folderName;
    private final Duration runtime;
    private final String cleanTitle;
    private final String imdbId;
    private final int tmdbId;
    private final String titleSlug;
    private final File rootFolderPath;
    private final File folder;
    private final String certification;
    private final PSet<@NotNull String> genres;
    private final PSet<@NotNull String> keywords;
    private final IntSet tags = new IntArraySet();
    private final Instant added;
    private final AddMovieOptions addOptions;
    private final Ratings ratings;
    private final MovieFileResource movieFile;
    private final MovieCollectionResource collection;
    private final float popularity;
    private final Instant lastSearchTime;
    private final MovieStatisticsResource statistics;

    public MovieResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.title = StringParser.get(obj, "title");
        this.originalTitle = StringParser.get(obj, "originalTitle");
        this.originalLanguage = ObjectParser.get(Language.class, api, obj, "language");

        JSONArray alternateTitles = obj.has("alternateTitles") && obj.get("alternateTitles") != null ? obj.getJSONArray("alternateTitles") : null;
        List<@NotNull AlternativeTitleResource> alternateTitlesL = new ArrayList<>();
        if (alternateTitles != null) {
            for (int i = 0; i < alternateTitles.length(); i++) {
                alternateTitlesL.add(new AlternativeTitleResource(api, alternateTitles.getJSONObject(i)));
            }
        }
        this.alternateTitles = TreePVector.from(alternateTitlesL);

        this.secondaryYear = NumberParser.getInt(-1, obj, "secondaryYear");
        this.secondaryYearSourceId = NumberParser.getInt(-1, obj, "secondaryYearSourceId");
        this.sortTitle = StringParser.get(obj, "sortTitle");
        this.sizeOnDisk = NumberParser.getLong(-1L, obj, "sizeOnDisk");
        this.status = MovieStatusType.get(MovieStatusType.DELETED, obj, "status");
        this.overview = StringParser.get(obj, "overview");
        this.inCinemas = InstantParser.get(obj, "inCinemas");
        this.physicalRelease = InstantParser.get(obj, "physicalRelease");
        this.digitalRelease = InstantParser.get(obj, "digitalRelease");
        this.releaseDate = InstantParser.get(obj, "releaseDate");
        this.physicalReleaseNote = StringParser.get(obj, "physicalReleaseNote");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        this.website = StringParser.get(obj, "website");
        this.remotePoster = StringParser.get(obj, "remotePoster");
        this.year = NumberParser.getInt(-1, obj, "year");
        this.youTubeTrailerId = StringParser.get(obj, "youTubeTrailerId");
        this.studio = StringParser.get(obj, "studio");
        this.path = FileParser.get(obj, "path");
        this.qualityProfileId = NumberParser.getInt(-1, obj, "qualityProfileId");
        this.hasFile = BooleanParser.get(false, obj, "hasFile");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.minimumAvailability = MovieStatusType.get(MovieStatusType.DELETED, obj, "minimumAvailability");
        this.isAvailable = BooleanParser.get(false, obj, "isAvailable");
        this.folderName = StringParser.get(obj, "folderName");
        this.runtime = DurationParser.get(obj, "runtime");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");
        this.imdbId = StringParser.get(obj, "imdbId");
        this.tmdbId = NumberParser.getInt(-1, obj, "tmdbId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.rootFolderPath = FileParser.get(obj, "rootFolderPath");
        this.folder = FileParser.get(obj, "folder");
        this.certification = StringParser.get(obj, "certification");

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        JSONArray keywords = obj.has("keywords") && obj.get("keywords") != null ? obj.getJSONArray("keywords") : null;
        Set<@NotNull String> keywordsL = new HashSet<>();
        if (keywords != null) {
            for (int i = 0; i < keywords.length(); i++) {
                keywordsL.add(keywords.getString(i));
            }
        }
        this.keywords = TreePSet.from(keywordsL);

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        this.added = InstantParser.get(Instant.EPOCH, obj, "added");
        this.addOptions = ObjectParser.get(AddMovieOptions.class, api, obj, "addOptions");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.movieFile = ObjectParser.get(MovieFileResource.class, api, obj, "movieFile");
        this.collection = ObjectParser.get(MovieCollectionResource.class, api, obj, "collection");
        this.popularity = NumberParser.getFloat(-1.0F, obj, "popularity");
        this.lastSearchTime = InstantParser.get(Instant.EPOCH, obj, "lastSearchTime");
        this.statistics = ObjectParser.get(MovieStatisticsResource.class, api, obj, "statistics");
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String originalTitle() {
        return originalTitle;
    }

    public @Nullable Language originalLanguage() {
        return originalLanguage;
    }

    public @NotNull PVector<@NotNull AlternativeTitleResource> alternateTitles() {
        return alternateTitles;
    }

    public int secondaryYear() {
        return secondaryYear;
    }

    public int secondaryYearSourceId() {
        return secondaryYearSourceId;
    }

    public @Nullable String sortTitle() {
        return sortTitle;
    }

    public long sizeOnDisk() {
        return sizeOnDisk;
    }

    public @NotNull MovieStatusType status() {
        return status;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable Instant inCinemas() {
        return inCinemas;
    }

    public @Nullable Instant physicalRelease() {
        return physicalRelease;
    }

    public @Nullable Instant digitalRelease() {
        return digitalRelease;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public @Nullable String physicalReleaseNote() {
        return physicalReleaseNote;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @Nullable String website() {
        return website;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public int year() {
        return year;
    }

    public @Nullable String youTubeTrailerId() {
        return youTubeTrailerId;
    }

    public @Nullable String studio() {
        return studio;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityProfile qualityProfile() {
        return api.fetch(QualityProfile.class, qualityProfileId);
    }

    public boolean hasFile() {
        return hasFile;
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull MovieStatusType minimumAvailability() {
        return minimumAvailability;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public @Nullable String folderName() {
        return folderName;
    }

    public @NotNull Duration runtime() {
        return runtime;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public @Nullable String imdbId() {
        return imdbId;
    }

    public int tmdbId() {
        return tmdbId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable File folder() {
        return folder;
    }

    public @Nullable String certification() {
        return certification;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @NotNull PSet<@NotNull String> keywords() {
        return keywords;
    }

    public @NotNull PVector<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return TreePVector.from(r);
    }

    public @NotNull Instant added() {
        return added;
    }

    public @Nullable AddMovieOptions addOptions() {
        return addOptions;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable MovieFileResource movieFile() {
        return movieFile;
    }

    public @Nullable MovieCollectionResource collection() {
        return collection;
    }

    public float popularity() {
        return popularity;
    }

    public @NotNull Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @Nullable MovieStatisticsResource statistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MovieResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MovieResource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", originalLanguage=" + originalLanguage +
                ", alternateTitles=" + alternateTitles +
                ", secondaryYear=" + secondaryYear +
                ", secondaryYearSourceId=" + secondaryYearSourceId +
                ", sortTitle='" + sortTitle + '\'' +
                ", sizeOnDisk=" + sizeOnDisk +
                ", status=" + status +
                ", overview='" + overview + '\'' +
                ", inCinemas=" + inCinemas +
                ", physicalRelease=" + physicalRelease +
                ", digitalRelease=" + digitalRelease +
                ", releaseDate=" + releaseDate +
                ", physicalReleaseNote='" + physicalReleaseNote + '\'' +
                ", images=" + images +
                ", website='" + website + '\'' +
                ", remotePoster='" + remotePoster + '\'' +
                ", year=" + year +
                ", youTubeTrailerId='" + youTubeTrailerId + '\'' +
                ", studio='" + studio + '\'' +
                ", path=" + path +
                ", qualityProfileId=" + qualityProfileId +
                ", hasFile=" + hasFile +
                ", monitored=" + monitored +
                ", minimumAvailability=" + minimumAvailability +
                ", isAvailable=" + isAvailable +
                ", folderName='" + folderName + '\'' +
                ", runtime=" + runtime +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", tmdbId=" + tmdbId +
                ", titleSlug='" + titleSlug + '\'' +
                ", rootFolderPath=" + rootFolderPath +
                ", folder=" + folder +
                ", certification='" + certification + '\'' +
                ", genres=" + genres +
                ", keywords=" + keywords +
                ", tags=" + tags +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", ratings=" + ratings +
                ", movieFile=" + movieFile +
                ", collection=" + collection +
                ", popularity=" + popularity +
                ", lastSearchTime=" + lastSearchTime +
                ", statistics=" + statistics +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
