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
    private final Set<@NotNull String> secondaryTypes = new HashSet<>();
    private final int mediumCount;
    private final Ratings ratings;
    private final Instant releaseDate;
    private final List<@NotNull AlbumReleaseResource> releases = new ArrayList<>();
    private final Set<@NotNull String> genres = new HashSet<>();
    private final List<@NotNull MediumResource> media = new ArrayList<>();
    private final List<@NotNull MediaCover> images = new ArrayList<>();
    private final List<@NotNull Links> links = new ArrayList<>();
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
        if (secondaryTypes != null) {
            for (int i = 0; i < secondaryTypes.length(); i++) {
                this.secondaryTypes.add(secondaryTypes.getString(i));
            }
        }

        this.mediumCount = NumberParser.getInt(-1, obj, "mediumCount");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.releaseDate = InstantParser.get(obj, "releaseDate");

        JSONArray releases = obj.has("releases") && obj.get("releases") != null ? obj.getJSONArray("releases") : null;
        if (releases != null) {
            for (int i = 0; i < releases.length(); i++) {
                this.releases.add(new AlbumReleaseResource(api, releases.getJSONObject(i)));
            }
        }

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                this.genres.add(genres.getString(i));
            }
        }

        JSONArray media = obj.has("media") && obj.get("media") != null ? obj.getJSONArray("media") : null;
        if (media != null) {
            for (int i = 0; i < media.length(); i++) {
                this.media.add(new MediumResource(api, media.getJSONObject(i)));
            }
        }

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                this.images.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                this.links.add(new Links(api, links.getJSONObject(i)));
            }
        }

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

    public @NotNull Set<@NotNull String> secondaryTypes() {
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

    public @NotNull List<@NotNull AlbumReleaseResource> releases() {
        return releases;
    }

    public @NotNull Set<@NotNull String> genres() {
        return genres;
    }

    public @NotNull List<@NotNull MediumResource> media() {
        return media;
    }

    public @NotNull List<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull List<@NotNull Links> links() {
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
