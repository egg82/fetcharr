package me.egg82.fetcharr.web.model.radarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AllMovies extends AbstractAPIObject<AllMovies> {
    public static AllMovies UNKNOWN = new AllMovies(ArrAPI.UNKNOWN);

    private final Set<@NotNull Movie> items = new HashSet<>();

    public AllMovies(@NotNull ArrAPI api) {
        super(api, "/api/v3/movie");
    }

    @Override
    public AllMovies fetch(@NotNull String apiKey) {
        if (!this.fetching.compareAndSet(false, true)) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile());
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile();
            try {
                parse(data.read());
                if (!this.items.isEmpty()) {
                    this.fetched = meta.fetched();
                    this.fetching.set(false);
                    return this;
                }
            } catch (Exception ex) {
                logger.warn("Could not read data from {}", data.path(), ex);
            }
        }

        JsonNode node = get(apiKey);
        if (node == null) {
            logger.warn("Could not read data from {}", url());
            // Not setting fetched = invalid
            this.fetching.set(false);
            return this;
        }

        try {
            parse(node);
        } catch (Exception ex) {
            logger.warn("Could not read data from {}", url(), ex);
            this.fetching.set(false);
            return this;
        }

        this.fetched = Instant.now();
        try {
            cacheFile().write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile().path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
        this.fetching.set(false);
        return this;
    }

    @Override
    public boolean valid() {
        if (this.fetched == null) {
            return false;
        }

        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        if (!useCache) {
            return false;
        }

        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);
        return this.fetched.plus(cacheTime.duration()).isAfter(Instant.now());
    }

    @Override
    public boolean unknown() {
        return false;
    }

    @Override
    public void invalidate() {
        try {
            cacheFile().delete();
            metaFile().delete();
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), ex);
        }
    }

    @Override
    protected void parse(@NotNull JsonNode data) {
        JSONArray arr = data.getArray();

        this.items.clear();
        if (arr == null || arr.isEmpty()) {
            return;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj == null || obj.isEmpty()) {
                continue;
            }

            int id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            if (id >= 0) {
                this.items.add(api.fetch(Movie.class, id, true));
            }
        }
    }

    public @NotNull Set<@NotNull Movie> items() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AllMovies allMovies)) return false;
        return Objects.equals(items, allMovies.items);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(items);
    }

    @Override
    public String toString() {
        return "AllMovies{" +
                "items=" + items +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }
}
