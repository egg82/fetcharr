package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.CacheMeta;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.parse.BooleanParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.web.ArrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class QualityProfile extends AbstractAPIObject<QualityProfile> {
    public static QualityProfile UNKNOWN = new QualityProfile(ArrAPI.UNKNOWN, -1);

    private final int id;

    private int cutoff;
    private int cutoffFormatScore;
    private final Set<@NotNull Format> formatItems = new HashSet<>();
    private final Set<@NotNull QualityResource> items = new HashSet<>();
    private int minFormatScore;
    private int minUpgradeFormatScore;
    private String name;
    private boolean upgradeAllowed;

    public QualityProfile(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/qualityprofile/" + id);
        this.id = id;
    }

    @Override
    public QualityProfile fetch(@NotNull String apiKey) {
        if (this.id < 0 || !this.fetching.compareAndSet(false, true)) {
            return this;
        }

        CacheMeta meta = new CacheMeta(metaFile(id));
        boolean useCache = ConfigVars.getBool(ConfigVars.USE_CACHE);
        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.LONG_CACHE_TIME);

        if (useCache && meta.fetched().plus(cacheTime.duration()).isAfter(Instant.now())) {
            JSONFile data = cacheFile(id);
            try {
                parse(data.read());
                if (this.name != null && !this.name.isBlank()) {
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
            cacheFile(id).write(node);
        } catch (IOException ex) {
            logger.warn("Could not write data to {}", cacheFile(id).path(), ex);
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

        TimeValue cacheTime = ConfigVars.getTimeValue(ConfigVars.LONG_CACHE_TIME);
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
        JSONObject obj = data.getObject();

        if (obj == null || obj.isEmpty()) {
            return;
        }

        this.cutoff = NumberParser.parseInt(-1, StringParser.parse(obj, "cutoff"));
        this.cutoffFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "cutoffFormatScore"));

        this.formatItems.clear();
        JSONArray formatItems = obj.has("formatItems") ? obj.getJSONArray("formatItems") : null;
        if (formatItems != null) {
            for (int i = 0; i < formatItems.length(); i++) {
                this.formatItems.add(new Format(formatItems.getJSONObject(i)));
            }
        }

        this.items.clear();
        JSONArray items = obj.has("items") ? obj.getJSONArray("items") : null;
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                this.items.add(new QualityResource(items.getJSONObject(i)));
            }
        }

        this.minFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "minFormatScore"));
        this.minUpgradeFormatScore = NumberParser.parseInt(-1, StringParser.parse(obj, "minUpgradeFormatScore"));
        this.name = StringParser.parse(obj, "name");
        this.upgradeAllowed = BooleanParser.parse(false, StringParser.parse(obj, "upgradeAllowed"));
    }

    public int id() {
        return id;
    }

    public int cutoff() {
        return cutoff;
    }

    public int cutoffFormatScore() {
        return cutoffFormatScore;
    }

    public @NotNull Set<@NotNull Format> formatItems() {
        return formatItems;
    }

    public @NotNull Set<@NotNull QualityResource> items() {
        return items;
    }

    public int minFormatScore() {
        return minFormatScore;
    }

    public int minUpgradeFormatScore() {
        return minUpgradeFormatScore;
    }

    public @Nullable String name() {
        return name;
    }

    public boolean upgradeAllowed() {
        return upgradeAllowed;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityProfile{" +
                "id=" + id +
                ", cutoff=" + cutoff +
                ", cutoffFormatScore=" + cutoffFormatScore +
                ", formatItems=" + formatItems +
                ", items=" + items +
                ", minFormatScore=" + minFormatScore +
                ", minUpgradeFormatScore=" + minUpgradeFormatScore +
                ", name='" + name + '\'' +
                ", upgradeAllowed=" + upgradeAllowed +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public class Format {
        private final int id;

        private final CustomFormat format;
        private final String name;
        private final int score;

        public Format(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));

            int format = NumberParser.parseInt(-1, StringParser.parse(obj, "format"));
            this.format = format >= 0 ? api.fetch(CustomFormat.class, format, false) : CustomFormat.UNKNOWN;
            this.score = NumberParser.parseInt(-1, StringParser.parse(obj, "score"));

            this.name = StringParser.parse(obj, "name");
        }

        public int id() {
            return id;
        }

        public @NotNull CustomFormat format() {
            return format;
        }

        public @Nullable String name() {
            return name;
        }

        public int score() {
            return score;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Format format)) return false;
            return id == format.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Format{" +
                    "id=" + id +
                    ", format=" + format +
                    ", name='" + name + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    public static class QualityResource {
        private final int id;

        private final boolean allowed;
        private final Set<@NotNull QualityResource> items = new HashSet<>();
        private final String name;
        private final Quality quality;

        public QualityResource(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.allowed = BooleanParser.parse(false, StringParser.parse(obj, "allowed"));

            JSONArray items = obj.has("items") ? obj.getJSONArray("items") : null;
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    this.items.add(new QualityResource(items.getJSONObject(i)));
                }
            }

            this.name = StringParser.parse(obj, "name");
            this.quality = obj.has("quality") ? new Quality(obj.getJSONObject("quality")) : null;
        }

        public int id() {
            return id;
        }

        public boolean allowed() {
            return allowed;
        }

        public @NotNull Set<@NotNull QualityResource> items() {
            return items;
        }

        public @Nullable String name() {
            return name;
        }

        public @Nullable Quality quality() {
            return quality;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof QualityResource that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "QualityResource{" +
                    "id=" + id +
                    ", allowed=" + allowed +
                    ", items=" + items +
                    ", name='" + name + '\'' +
                    ", quality=" + quality +
                    '}';
        }
    }
}
