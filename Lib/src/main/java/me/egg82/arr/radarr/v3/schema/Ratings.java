package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ratings extends AbstractAPIObject {
    private final RatingChild imdb;
    private final RatingChild tmdb;
    private final RatingChild metacritic;
    private final RatingChild rottenTomatoes;
    private final RatingChild trakt;

    public Ratings(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.imdb = new RatingChild(api, obj.getJSONObject("imdb"));
        this.tmdb = new RatingChild(api, obj.getJSONObject("tmdb"));
        this.metacritic = new RatingChild(api, obj.getJSONObject("metacritic"));
        this.rottenTomatoes = new RatingChild(api, obj.getJSONObject("rottenTomatoes"));
        this.trakt = new RatingChild(api, obj.getJSONObject("trakt"));
    }

    public @NotNull RatingChild imdb() {
        return imdb;
    }

    public @NotNull RatingChild tmdb() {
        return tmdb;
    }

    public @NotNull RatingChild metacritic() {
        return metacritic;
    }

    public @NotNull RatingChild rottenTomatoes() {
        return rottenTomatoes;
    }

    public @NotNull RatingChild trakt() {
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
