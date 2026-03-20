package me.egg82.arr.lidarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.MetadataProfile;
import me.egg82.arr.lidarr.v1.QualityProfile;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.parse.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class ArtistResource extends AbstractAPIObject {
    private final int id;
    private final ArtistStatusType status;
    private final boolean ended;
    private final String artistName;
    private final String foreignArtistId;
    private final String mbId;
    private final int tadbId;
    private final int discogsId;
    private final String allMusicId;
    private final String overview;
    private final String artistType;
    private final String disambiguation;
    private final PVector<@NotNull Links> links;
    private final AlbumResource nextAlbum;
    private final AlbumResource lastAlbum;
    private final PVector<@NotNull MediaCover> images;
    private final PVector<@NotNull Member> members;
    private final String remotePoster;
    private final File path;
    private final int qualityProfileId;
    private final int metadataProfileId;
    private final boolean monitored;
    private final NewItemMonitorTypes monitorNewItems;
    private final File rootFolderPath;
    private final File folder;
    private final PSet<@NotNull String> genres;
    private final String cleanName;
    private final String sortName;
    private final IntSet tags = new IntArraySet();
    private final Instant added;
    private final AddArtistOptions addOptions;
    private final Ratings ratings;
    private final ArtistStatisticsResource statistics;

    public ArtistResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.status = ArtistStatusType.get(ArtistStatusType.DELETED, obj, "status");
        this.ended = BooleanParser.get(false, obj, "ended");
        this.artistName = StringParser.get(obj, "artistName");
        this.foreignArtistId = StringParser.get(obj, "foreignArtistId");
        this.mbId = StringParser.get(obj, "mbId");
        this.tadbId = NumberParser.getInt(-1, obj, "tadbId");
        this.discogsId = NumberParser.getInt(-1, obj, "discogsId");
        this.allMusicId = StringParser.get(obj, "allMusicId");
        this.overview = StringParser.get(obj, "overview");
        this.artistType = StringParser.get(obj, "artistType");
        this.disambiguation = StringParser.get(obj, "disambiguation");

        JSONArray links = obj.has("links") && obj.get("links") != null ? obj.getJSONArray("links") : null;
        List<@NotNull Links> linksL = new ArrayList<>();
        if (links != null) {
            for (int i = 0; i < links.length(); i++) {
                linksL.add(new Links(api, links.getJSONObject(i)));
            }
        }
        this.links = TreePVector.from(linksL);

        this.nextAlbum = ObjectParser.get(AlbumResource.class, api, obj, "nextAlbum");
        this.lastAlbum = ObjectParser.get(AlbumResource.class, api, obj, "lastAlbum");

        JSONArray images = obj.has("images") && obj.get("images") != null ? obj.getJSONArray("images") : null;
        List<@NotNull MediaCover> imagesL = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imagesL.add(new MediaCover(api, images.getJSONObject(i)));
            }
        }
        this.images = TreePVector.from(imagesL);

        JSONArray members = obj.has("members") && obj.get("members") != null ? obj.getJSONArray("members") : null;
        List<@NotNull Member> membersL = new ArrayList<>();
        if (members != null) {
            for (int i = 0; i < members.length(); i++) {
                membersL.add(new Member(api, members.getJSONObject(i)));
            }
        }
        this.members = TreePVector.from(membersL);

        this.remotePoster = StringParser.get(obj, "remotePoster");
        this.path = FileParser.get(obj, "path");
        this.qualityProfileId = NumberParser.getInt(-1, obj, "qualityProfileId");
        this.metadataProfileId = NumberParser.getInt(-1, obj, "metadataProfileId");
        this.monitored = BooleanParser.get(false, obj, "monitored");
        this.monitorNewItems = NewItemMonitorTypes.get(NewItemMonitorTypes.NONE, obj, "monitorNewItems");
        this.rootFolderPath = FileParser.get(obj, "rootFolderPath");
        this.folder = FileParser.get(obj, "folder");

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        this.cleanName = StringParser.get(obj, "cleanName");
        this.sortName = StringParser.get(obj, "sortName");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        this.added = InstantParser.get(obj, "added");
        this.addOptions = ObjectParser.get(AddArtistOptions.class, api, obj, "addOptions");
        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
        this.statistics = ObjectParser.get(ArtistStatisticsResource.class, api, obj, "statistics");
    }

    public int id() {
        return id;
    }

    public @NotNull ArtistStatusType status() {
        return status;
    }

    public boolean ended() {
        return ended;
    }

    public @Nullable String artistName() {
        return artistName;
    }

    public @Nullable String foreignArtistId() {
        return foreignArtistId;
    }

    public @Nullable String mbId() {
        return mbId;
    }

    public int tadbId() {
        return tadbId;
    }

    public int discogsId() {
        return discogsId;
    }

    public @Nullable String allMusicId() {
        return allMusicId;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable String artistType() {
        return artistType;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @Nullable AlbumResource nextAlbum() {
        return nextAlbum;
    }

    public @Nullable AlbumResource lastAlbum() {
        return lastAlbum;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull PVector<@NotNull Member> members() {
        return members;
    }

    public @Nullable String remotePoster() {
        return remotePoster;
    }

    public @Nullable File path() {
        return path;
    }

    public @Nullable QualityProfile qualityProfile() {
        return api.fetch(QualityProfile.class, qualityProfileId);
    }

    public @Nullable MetadataProfile metadataProfile() {
        return api.fetch(MetadataProfile.class, metadataProfileId);
    }

    public boolean monitored() {
        return monitored;
    }

    public @NotNull NewItemMonitorTypes monitorNewItems() {
        return monitorNewItems;
    }

    public @Nullable File rootFolderPath() {
        return rootFolderPath;
    }

    public @Nullable File folder() {
        return folder;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @Nullable String cleanName() {
        return cleanName;
    }

    public @Nullable String sortName() {
        return sortName;
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

    public @Nullable Instant added() {
        return added;
    }

    public @Nullable AddArtistOptions addOptions() {
        return addOptions;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    public @Nullable ArtistStatisticsResource statistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArtistResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ArtistResource{" +
                "id=" + id +
                ", status=" + status +
                ", ended=" + ended +
                ", artistName='" + artistName + '\'' +
                ", foreignArtistId='" + foreignArtistId + '\'' +
                ", mbId='" + mbId + '\'' +
                ", tadbId=" + tadbId +
                ", discogsId=" + discogsId +
                ", allMusicId='" + allMusicId + '\'' +
                ", overview='" + overview + '\'' +
                ", artistType='" + artistType + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", links=" + links +
                ", nextAlbum=" + nextAlbum +
                ", lastAlbum=" + lastAlbum +
                ", images=" + images +
                ", members=" + members +
                ", remotePoster='" + remotePoster + '\'' +
                ", path=" + path +
                ", qualityProfileId=" + qualityProfileId +
                ", metadataProfileId=" + metadataProfileId +
                ", monitored=" + monitored +
                ", monitorNewItems=" + monitorNewItems +
                ", rootFolderPath=" + rootFolderPath +
                ", folder=" + folder +
                ", genres=" + genres +
                ", cleanName='" + cleanName + '\'' +
                ", sortName='" + sortName + '\'' +
                ", tags=" + tags +
                ", added=" + added +
                ", addOptions=" + addOptions +
                ", ratings=" + ratings +
                ", statistics=" + statistics +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
