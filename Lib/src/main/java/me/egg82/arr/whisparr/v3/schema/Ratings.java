package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.ObjectParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Ratings extends AbstractAPIObject {
    private final RatingChild imdb;
    private final RatingChild tmdb;
    private final RatingChild metacritic;
    private final RatingChild rottenTomatoes;
    private final RatingChild trakt;

    public Ratings(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.imdb = ObjectParser.get(RatingChild.class, api, obj, "imdb");
        this.tmdb = ObjectParser.get(RatingChild.class, api, obj, "tmdb");
        this.metacritic = ObjectParser.get(RatingChild.class, api, obj, "metacritic");
        this.rottenTomatoes = ObjectParser.get(RatingChild.class, api, obj, "rottenTomatoes");
        this.trakt = ObjectParser.get(RatingChild.class, api, obj, "trakt");
    }

    public @Nullable RatingChild imdb() {
        return imdb;
    }

    public @Nullable RatingChild tmdb() {
        return tmdb;
    }

    public @Nullable RatingChild metacritic() {
        return metacritic;
    }

    public @Nullable RatingChild rottenTomatoes() {
        return rottenTomatoes;
    }

    public @Nullable RatingChild trakt() {
        return trakt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ratings ratings)) return false;
        return Objects.equals(imdb, ratings.imdb) && Objects.equals(tmdb, ratings.tmdb) && Objects.equals(metacritic, ratings.metacritic) && Objects.equals(rottenTomatoes, ratings.rottenTomatoes) && Objects.equals(trakt, ratings.trakt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imdb, tmdb, metacritic, rottenTomatoes, trakt);
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "imdb=" + imdb +
                ", tmdb=" + tmdb +
                ", metacritic=" + metacritic +
                ", rottenTomatoes=" + rottenTomatoes +
                ", trakt=" + trakt +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
