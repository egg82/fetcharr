package me.egg82.fetcharr.web.model.radarr;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Ratings {
    private final Rating imdb;
    private final Rating tmdb;
    private final Rating metacritic;
    private final Rating rottenTomatoes;
    private final Rating trakt;

    public Ratings(@NotNull JSONObject obj) {
        this.imdb = obj.has("imdb") ? new Rating(obj.getJSONObject("imdb")) : null;
        this.tmdb = obj.has("tmdb") ? new Rating(obj.getJSONObject("tmdb")) : null;
        this.metacritic = obj.has("metacritic") ? new Rating(obj.getJSONObject("metacritic")) : null;
        this.rottenTomatoes = obj.has("rottenTomatoes") ? new Rating(obj.getJSONObject("rottenTomatoes")) : null;
        this.trakt = obj.has("trakt") ? new Rating(obj.getJSONObject("trakt")) : null;
    }

    public @Nullable Rating imdb() {
        return imdb;
    }

    public @Nullable Rating tmdb() {
        return tmdb;
    }

    public @Nullable Rating metacritic() {
        return metacritic;
    }

    public @Nullable Rating rottenTomatoes() {
        return rottenTomatoes;
    }

    public @Nullable Rating trakt() {
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
                '}';
    }

    public static class Rating {
        private final int votes;
        private final float value;
        private final RatingType type;

        public Rating(@NotNull JSONObject obj) {
            this.votes = NumberParser.parseInt(-1, StringParser.parse(obj, "votes"));
            this.value = NumberParser.parseFloat(-1.0F, StringParser.parse(obj, "value"));
            this.type = RatingType.parse(RatingType.USER, StringParser.parse(obj, "type"));
        }

        public int votes() {
            return votes;
        }

        public float value() {
            return value;
        }

        public @NotNull RatingType type() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Rating rating)) return false;
            return votes == rating.votes && Float.compare(value, rating.value) == 0 && type == rating.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(votes, value, type);
        }

        @Override
        public String toString() {
            return "Rating{" +
                    "votes=" + votes +
                    ", value=" + value +
                    ", type=" + type +
                    '}';
        }
    }
}
