package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Artist;
import me.egg82.arr.lidarr.v1.ReleaseProfile;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AlbumResource extends AbstractAPIObject {
    private final int id;
    private final String title;
    private final String disambiguation;
    private final String overview;
    private final int artistId;
    private final String foreignAlbumId;
    private final boolean monitored;
    private final boolean anyReleaseOk;
    private final int profileId;
    private final Duration duration;
    private final String albumType;
    private final PSet<@NotNull String> secondaryTypes;
    private final int mediumCount;
    private final Ratings ratings;
    private final Instant releaseDate;
    private final PVector<@NotNull AlbumReleaseResource> releases;
    private final PSet<@NotNull String> genres;
    private final PVector<@NotNull MediumResource> media;
    private final PVector<@NotNull MediaCover> images;
    private final PVector<@NotNull Links> links;
    private final Instant lastSearchTime;
    private final AlbumStatisticsResource statistics;
    private final AddAlbumOptions addOptions;
    private final String remoteCover;

    public AlbumResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.title = StringParser.get(obj, "title");
        this.disambiguation = StringParser.get(obj, "disambiguation");
        this.overview = StringParser.get(obj, "overview");
        this.artistId = NumberParser.getInt(-1, obj, "artistId");
        this.foreignAlbumId = StringParser.get(obj, "foreignAlbumId");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.anyReleaseOk = BooleanParser.get(false, obj, "anyReleaseOk");
        this.profileId = NumberParser.getInt(-1, obj, "profileId");
        this.duration = DurationParser.get(obj, "duration");
        this.albumType = StringParser.get(obj, "albumType");

        JSONArray secondaryTypes = obj.has("secondaryTypes") && obj.get("secondaryTypes") != null ? obj.getJSONArray("secondaryTypes") : null;
        Set<@NotNull String> secondaryTypesL = new HashSet<>();
        if (secondaryTypes != null) {
            for (int i = 0; i < secondaryTypes.length(); i++) {
                secondaryTypesL.add(secondaryTypes.getString(i));
            }
        }
        this.secondaryTypes = TreePSet.from(secondaryTypesL);

        this.mediumCount = NumberParser.getInt(-1, obj, "mediumCount");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.releaseDate = InstantParser.get(obj, "releaseDate");

        JSONArray releases = obj.has("releases") && obj.get("releases") != null ? obj.getJSONArray("releases") : null;
        List<@NotNull AlbumReleaseResource> releasesL = new ArrayList<>();
        if (releases != null) {
            for (int i = 0; i < releases.length(); i++) {
                releasesL.add(new AlbumReleaseResource(api, releases.getJSONObject(i)));
            }
        }
        this.releases = TreePVector.from(releasesL);

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        JSONArray media = obj.has("media") && obj.get("media") != null ? obj.getJSONArray("media") : null;
        List<@NotNull MediumResource> mediaL = new ArrayList<>();
        if (media != null) {
            for (int i = 0; i < media.length(); i++) {
                mediaL.add(new MediumResource(api, media.getJSONObject(i)));
            }
        }
        this.media = TreePVector.from(mediaL);

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull Links> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new Links(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);

        this.lastSearchTime = InstantParser.get(obj, "lastSearchTime");
        this.statistics = ObjectParser.get(AlbumStatisticsResource.class, api, obj, "statistics");
        this.addOptions = ObjectParser.get(AddAlbumOptions.class, api, obj, "addOptions");
        this.remoteCover = StringParser.get(obj, "remoteCover");
    }

    public int id() {
        return id;
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable Artist artist() {
        return api.fetch(Artist.class, artistId);
    }

    public @Nullable String foreignAlbumId() {
        return foreignAlbumId;
    }

    public boolean monitored() {
        return monitored;
    }

    public boolean anyReleaseOk() {
        return anyReleaseOk;
    }

    public @Nullable ReleaseProfile profile() {
        return api.fetch(ReleaseProfile.class, profileId);
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public @Nullable String albumType() {
        return albumType;
    }

    public @NotNull PSet<@NotNull String> secondaryTypes() {
        return secondaryTypes;
    }

    public int mediumCount() {
        return mediumCount;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable Instant releaseDate() {
        return releaseDate;
    }

    public @NotNull PVector<@NotNull AlbumReleaseResource> releases() {
        return releases;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @NotNull PVector<@NotNull MediumResource> media() {
        return media;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @Nullable Instant lastSearchTime() {
        return lastSearchTime;
    }

    public @Nullable AlbumStatisticsResource statistics() {
        return statistics;
    }

    public @Nullable AddAlbumOptions addOptions() {
        return addOptions;
    }

    public @Nullable String remoteCover() {
        return remoteCover;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlbumResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlbumResource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", overview='" + overview + '\'' +
                ", artistId=" + artistId +
                ", foreignAlbumId='" + foreignAlbumId + '\'' +
                ", monitored=" + monitored +
                ", anyReleaseOk=" + anyReleaseOk +
                ", profileId=" + profileId +
                ", duration=" + duration +
                ", albumType='" + albumType + '\'' +
                ", secondaryTypes=" + secondaryTypes +
                ", mediumCount=" + mediumCount +
                ", ratings=" + ratings +
                ", releaseDate=" + releaseDate +
                ", releases=" + releases +
                ", genres=" + genres +
                ", media=" + media +
                ", images=" + images +
                ", links=" + links +
                ", lastSearchTime=" + lastSearchTime +
                ", statistics=" + statistics +
                ", addOptions=" + addOptions +
                ", remoteCover='" + remoteCover + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
