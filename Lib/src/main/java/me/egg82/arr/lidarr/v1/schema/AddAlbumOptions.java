package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddAlbumOptions extends AbstractAPIObject {
    private final AlbumAddType addType;
    private final boolean searchForNewAlbum;

    public AddAlbumOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.addType = AlbumAddType.get(AlbumAddType.MANUAL, obj, "addType");
        this.searchForNewAlbum = BooleanParser.get(false, obj, "searchForNewAlbum");
    }

    public @NotNull AlbumAddType addType() {
        return addType;
    }

    public boolean searchForNewAlbum() {
        return searchForNewAlbum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddAlbumOptions that)) return false;
        return searchForNewAlbum == that.searchForNewAlbum && addType == that.addType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addType, searchForNewAlbum);
    }

    @Override
    public String toString() {
        return "AddAlbumOptions{" +
                "addType=" + addType +
                ", searchForNewAlbum=" + searchForNewAlbum +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
