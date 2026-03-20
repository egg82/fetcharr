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
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetadataProfileResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final PVector<@NotNull PrimaryAlbumType> primaryAlbumTypes;
    private final PVector<@NotNull SecondaryAlbumType> secondaryAlbumTypes;
    private final ProfileReleaseStatusItemResource releaseStatuses;

    public MetadataProfileResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");

        JSONArray primaryAlbumTypes = obj.has("primaryAlbumTypes") && obj.get("primaryAlbumTypes") != null ? obj.getJSONArray("primaryAlbumTypes") : null;
        List<@NotNull PrimaryAlbumType> primaryAlbumTypesL = new ArrayList<>();
        if (primaryAlbumTypes != null) {
            for (int i = 0; i < primaryAlbumTypes.length(); i++) {
                primaryAlbumTypesL.add(new PrimaryAlbumType(api, primaryAlbumTypes.getJSONObject(i)));
            }
        }
        this.primaryAlbumTypes = TreePVector.from(primaryAlbumTypesL);

        JSONArray secondaryAlbumTypes = obj.has("secondaryAlbumTypes") && obj.get("secondaryAlbumTypes") != null ? obj.getJSONArray("secondaryAlbumTypes") : null;
        List<@NotNull SecondaryAlbumType> secondaryAlbumTypesL = new ArrayList<>();
        if (secondaryAlbumTypes != null) {
            for (int i = 0; i < secondaryAlbumTypes.length(); i++) {
                secondaryAlbumTypesL.add(new SecondaryAlbumType(api, secondaryAlbumTypes.getJSONObject(i)));
            }
        }
        this.secondaryAlbumTypes = TreePVector.from(secondaryAlbumTypesL);

        this.releaseStatuses = ObjectParser.get(ProfileReleaseStatusItemResource.class, api, obj, "releaseStatuses");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull PVector<@NotNull PrimaryAlbumType> primaryAlbumTypes() {
        return primaryAlbumTypes;
    }

    public @NotNull PVector<@NotNull SecondaryAlbumType> secondaryAlbumTypes() {
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
