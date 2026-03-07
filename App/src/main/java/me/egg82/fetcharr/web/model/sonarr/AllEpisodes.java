package me.egg82.fetcharr.web.model.sonarr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AllEpisodes extends AbstractAPIObject<AllEpisodes> {
    public static AllEpisodes UNKNOWN = new AllEpisodes(ArrAPI.UNKNOWN, -1);

    private final int id;

    private final Set<@NotNull Episode> items = new HashSet<>();

    public AllEpisodes(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/episode?seriesId=" + id);
        this.id = id;
    }

    @Override
    public AllEpisodes fetch(@NotNull String apiKey) {
        if (this.id < 0) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile(id));
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.SHORT_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile(id);
            try {
                parse(data.read());
                if (!this.items.isEmpty()) {
                    this.fetched = meta.fetched();
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
            return this;
        }

        parse(node);
        this.fetched = Instant.now();
        try {
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
        }
        meta.setFetched(this.fetched);
        meta.write();
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
        return this.id < 0;
    }

    @Override
    public void invalidate() {
        try {
            cacheFile(id).delete();
            metaFile(id).delete();
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}-{}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), id, ex);
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
            int id = NumberParser.parseInt(-1, arr.getJSONObject(i).getString("id"));
            if (id >= 0) {
                this.items.add(api.fetch(Episode.class, id));
            }
        }
    }

    public int id() {
        return id;
    }

    public @NotNull Set<@NotNull Episode> items() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AllEpisodes that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AllEpisodes{" +
                "id=" + id +
                ", items=" + items +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }
}
