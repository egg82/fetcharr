package me.egg82.arr.readarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.InstantParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePSet;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.*;

public class AuthorMetadata extends AbstractAPIObject {
    private final int id;
    private final String foreignAuthorId;
    private final String titleSlug;
    private final String name;
    private final String sortName;
    private final String nameLastFirst;
    private final String sortNameLastFirst;
    private final PSet<@NotNull String> aliases;
    private final String overview;
    private final String disambiguation;
    private final String gender;
    private final String hometown;
    private final Instant born;
    private final Instant died;
    private final AuthorStatusType status;
    private final PVector<@NotNull MediaCover> images;
    private final PVector<@NotNull Links> links;
    private final PSet<@NotNull String> genres;
    private final Ratings ratings;

    public AuthorMetadata(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.foreignAuthorId = StringParser.get(obj, "foreignAuthorId");
        this.titleSlug = StringParser.get(obj, "titleSlug");
        this.name = StringParser.get(obj, "name");
        this.sortName = StringParser.get(obj, "sortName");
        this.nameLastFirst = StringParser.get(obj, "nameLastFirst");
        this.sortNameLastFirst = StringParser.get(obj, "sortNameLastFirst");

        JSONArray aliases = obj.has("aliases") && obj.get("aliases") != null ? obj.getJSONArray("aliases") : null;
        Set<@NotNull String> aliasesL = new HashSet<>();
        if (aliases != null) {
            for (int i = 0; i < aliases.length(); i++) {
                aliasesL.add(aliases.getString(i));
            }
        }
        this.aliases = TreePSet.from(aliasesL);

        this.overview = StringParser.get(obj, "overview");
        this.disambiguation = StringParser.get(obj, "disambiguation");
        this.gender = StringParser.get(obj, "gender");
        this.hometown = StringParser.get(obj, "hometown");
        this.born = InstantParser.get(obj, "born");
        this.died = InstantParser.get(obj, "died");
        this.status = AuthorStatusType.get(AuthorStatusType.ENDED, obj, "status");

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

        JSONArray genres = obj.has("genres") && obj.get("genres") != null ? obj.getJSONArray("genres") : null;
        Set<@NotNull String> genresL = new HashSet<>();
        if (genres != null) {
            for (int i = 0; i < genres.length(); i++) {
                genresL.add(genres.getString(i));
            }
        }
        this.genres = TreePSet.from(genresL);

        this.ratings = ObjectParser.get(Ratings.class, api, obj, "ratings");
    }

    public int id() {
        return id;
    }

    public @Nullable String foreignAuthorId() {
        return foreignAuthorId;
    }

    public @Nullable String titleSlug() {
        return titleSlug;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String sortName() {
        return sortName;
    }

    public @Nullable String nameLastFirst() {
        return nameLastFirst;
    }

    public @Nullable String sortNameLastFirst() {
        return sortNameLastFirst;
    }

    public @NotNull PSet<@NotNull String> aliases() {
        return aliases;
    }

    public @Nullable String overview() {
        return overview;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable String gender() {
        return gender;
    }

    public @Nullable String hometown() {
        return hometown;
    }

    public @Nullable Instant born() {
        return born;
    }

    public @Nullable Instant died() {
        return died;
    }

    public @NotNull AuthorStatusType status() {
        return status;
    }

    public @NotNull PVector<@NotNull MediaCover> images() {
        return images;
    }

    public @NotNull PVector<@NotNull Links> links() {
        return links;
    }

    public @NotNull PSet<@NotNull String> genres() {
        return genres;
    }

    public @Nullable Ratings ratings() {
        return ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthorMetadata that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AuthorMetadata{" +
                "id=" + id +
                ", foreignAuthorId='" + foreignAuthorId + '\'' +
                ", titleSlug='" + titleSlug + '\'' +
                ", name='" + name + '\'' +
                ", sortName='" + sortName + '\'' +
                ", nameLastFirst='" + nameLastFirst + '\'' +
                ", sortNameLastFirst='" + sortNameLastFirst + '\'' +
                ", aliases=" + aliases +
                ", overview='" + overview + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", gender='" + gender + '\'' +
                ", hometown='" + hometown + '\'' +
                ", born=" + born +
                ", died=" + died +
                ", status=" + status +
                ", images=" + images +
                ", links=" + links +
                ", genres=" + genres +
                ", ratings=" + ratings +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
