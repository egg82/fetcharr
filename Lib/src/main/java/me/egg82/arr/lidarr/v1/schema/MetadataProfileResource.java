package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetadataProfileResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final List<@NotNull PrimaryAlbumType> primaryAlbumTypes = new ArrayList<>();
    private final List<@NotNull SecondaryAlbumType> secondaryAlbumTypes = new ArrayList<>();
    private final ProfileReleaseStatusItemResource releaseStatuses;

    public MetadataProfileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");

        JSONArray primaryAlbumTypes = obj.has("primaryAlbumTypes") && obj.get("primaryAlbumTypes") != null ? obj.getJSONArray("primaryAlbumTypes") : null;
        if (primaryAlbumTypes != null) {
            for (int i = 0; i < primaryAlbumTypes.length(); i++) {
                this.primaryAlbumTypes.add(new PrimaryAlbumType(api, primaryAlbumTypes.getJSONObject(i)));
            }
        }

        JSONArray secondaryAlbumTypes = obj.has("secondaryAlbumTypes") && obj.get("secondaryAlbumTypes") != null ? obj.getJSONArray("secondaryAlbumTypes") : null;
        if (secondaryAlbumTypes != null) {
            for (int i = 0; i < secondaryAlbumTypes.length(); i++) {
                this.secondaryAlbumTypes.add(new SecondaryAlbumType(api, secondaryAlbumTypes.getJSONObject(i)));
            }
        }

        this.releaseStatuses = ObjectParser.get(ProfileReleaseStatusItemResource.class, api, obj, "releaseStatuses");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull List<@NotNull PrimaryAlbumType> primaryAlbumTypes() {
        return primaryAlbumTypes;
    }

    public @NotNull List<@NotNull SecondaryAlbumType> secondaryAlbumTypes() {
        return secondaryAlbumTypes;
    }

    public @Nullable ProfileReleaseStatusItemResource releaseStatuses() {
        return releaseStatuses;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MetadataProfileResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MetadataProfileResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryAlbumTypes=" + primaryAlbumTypes +
                ", secondaryAlbumTypes=" + secondaryAlbumTypes +
                ", releaseStatuses=" + releaseStatuses +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
