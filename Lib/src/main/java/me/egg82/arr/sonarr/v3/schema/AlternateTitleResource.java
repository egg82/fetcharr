package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AlternateTitleResource extends AbstractAPIObject {
    private final String comment;
    private final String sceneOrigin;
    private final int sceneSeasonNumber;
    private final int seasonNumber;
    private final String title;

    public AlternateTitleResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.comment = StringParser.get(obj, "comment");
        this.sceneOrigin = StringParser.get(obj, "sceneOrigin");
        this.sceneSeasonNumber = NumberParser.getInt(-1, obj, "sceneSeasonNumber");
        this.seasonNumber = NumberParser.getInt(-1, obj, "seasonNumber");
        this.title = StringParser.get(obj, "title");
    }

    public @Nullable String getComment() {
        return comment;
    }

    public @Nullable String getSceneOrigin() {
        return sceneOrigin;
    }

    public int getSceneSeasonNumber() {
        return sceneSeasonNumber;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public @Nullable String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlternateTitleResource that)) return false;
        return sceneSeasonNumber == that.sceneSeasonNumber && seasonNumber == that.seasonNumber && Objects.equals(comment, that.comment) && Objects.equals(sceneOrigin, that.sceneOrigin) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, sceneOrigin, sceneSeasonNumber, seasonNumber, title);
    }

    @Override
    public String toString() {
        return "AlternateTitleResource{" +
                "comment='" + comment + '\'' +
                ", sceneOrigin='" + sceneOrigin + '\'' +
                ", sceneSeasonNumber=" + sceneSeasonNumber +
                ", seasonNumber=" + seasonNumber +
                ", title='" + title + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
