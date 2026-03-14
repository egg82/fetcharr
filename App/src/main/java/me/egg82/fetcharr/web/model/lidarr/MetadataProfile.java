package me.egg82.fetcharr.web.model.lidarr;

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
import me.egg82.fetcharr.web.model.common.AbstractAPIObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MetadataProfile extends AbstractAPIObject<MetadataProfile> {
    public static final MetadataProfile UNKNOWN = new MetadataProfile(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String name;
    private final Set<@NotNull AlbumTypeItem> primaryAlbumTypes = new HashSet<>();
    private final Set<@NotNull AlbumTypeItem> secondaryAlbumTypes = new HashSet<>();
    private final Set<@NotNull ReleaseStatusItem> releaseStatuses = new HashSet<>();

    public MetadataProfile(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/metadataprofile/" + id);
        this.id = id;
    }

    @Override
    public MetadataProfile fetch(@NotNull String apiKey) {
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
            logger.debug("Could not read data from {}", url());
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
        } catch (IOException ex) {
            logger.warn("Could not delete cache files for {}-{} {}-{}", api.type().name().toLowerCase(), api.id(), getClass().getSimpleName(), id, ex);
        }
        this.fetched = null;
    }

    @Override
    protected void parse(@NotNull JsonNode data) {
        JSONObject obj = data.getObject();

        if (obj == null || obj.isEmpty()) {
            return;
        }

        this.primaryAlbumTypes.clear();
        JSONArray primaryAlbumTypes = obj.has("primaryAlbumTypes") ? obj.getJSONArray("primaryAlbumTypes") : null;
        if (primaryAlbumTypes != null) {
            for (int i = 0; i < primaryAlbumTypes.length(); i++) {
                this.primaryAlbumTypes.add(new AlbumTypeItem(primaryAlbumTypes.getJSONObject(i)));
            }
        }

        this.secondaryAlbumTypes.clear();
        JSONArray secondaryAlbumTypes = obj.has("secondaryAlbumTypes") ? obj.getJSONArray("secondaryAlbumTypes") : null;
        if (secondaryAlbumTypes != null) {
            for (int i = 0; i < secondaryAlbumTypes.length(); i++) {
                this.secondaryAlbumTypes.add(new AlbumTypeItem(secondaryAlbumTypes.getJSONObject(i)));
            }
        }

        this.releaseStatuses.clear();
        JSONArray releaseStatuses = obj.has("releaseStatuses") ? obj.getJSONArray("releaseStatuses") : null;
        if (releaseStatuses != null) {
            for (int i = 0; i < releaseStatuses.length(); i++) {
                this.releaseStatuses.add(new ReleaseStatusItem(releaseStatuses.getJSONObject(i)));
            }
        }

        this.name = StringParser.parse(obj, "name");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull Set<@NotNull AlbumTypeItem> primaryAlbumTypes() {
        return primaryAlbumTypes;
    }

    public @NotNull Set<@NotNull AlbumTypeItem> secondaryAlbumTypes() {
        return secondaryAlbumTypes;
    }

    public @NotNull Set<@NotNull ReleaseStatusItem> releaseStatuses() {
        return releaseStatuses;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MetadataProfile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MetadataProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryAlbumTypes=" + primaryAlbumTypes +
                ", secondaryAlbumTypes=" + secondaryAlbumTypes +
                ", releaseStatuses=" + releaseStatuses +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class AlbumTypeItem {
        private final int id;
        private final AlbumType albumType;
        private final boolean allowed;

        public AlbumTypeItem(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.albumType = obj.has("albumType") ? new AlbumType(obj.getJSONObject("albumType")) : null;
            this.allowed = BooleanParser.parse(false, StringParser.parse(obj, "allowed"));
        }

        public int id() {
            return id;
        }

        public @Nullable AlbumType albumType() {
            return albumType;
        }

        public boolean allowed() {
            return allowed;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AlbumTypeItem that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "AlbumTypeItem{" +
                    "id=" + id +
                    ", albumType=" + albumType +
                    ", allowed=" + allowed +
                    '}';
        }

        public static class AlbumType {
            private final int id;
            private final String name;

            public AlbumType(@NotNull JSONObject obj) {
                this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
                this.name = StringParser.parse(obj, "name");
            }

            public int id() {
                return id;
            }

            public @Nullable String name() {
                return name;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof AlbumType albumType)) return false;
                return id == albumType.id;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(id);
            }

            @Override
            public String toString() {
                return "AlbumType{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }

    public static class ReleaseStatusItem {
        private final int id;
        private final ReleaseStatus releaseStatus;
        private final boolean allowed;

        public ReleaseStatusItem(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
            this.releaseStatus = obj.has("releaseStatus") ? new ReleaseStatus(obj.getJSONObject("releaseStatus")) : null;
            this.allowed = BooleanParser.parse(false, StringParser.parse(obj, "allowed"));
        }

        public int id() {
            return id;
        }

        public @Nullable ReleaseStatus releaseStatus() {
            return releaseStatus;
        }

        public boolean allowed() {
            return allowed;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ReleaseStatusItem that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "ReleaseStatusItem{" +
                    "id=" + id +
                    ", releaseStatus=" + releaseStatus +
                    ", allowed=" + allowed +
                    '}';
        }

        public static class ReleaseStatus {
            private final int id;
            private final String name;

            public ReleaseStatus(@NotNull JSONObject obj) {
                this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));
                this.name = StringParser.parse(obj, "name");
            }

            public int id() {
                return id;
            }

            public @Nullable String name() {
                return name;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof ReleaseStatus that)) return false;
                return id == that.id;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(id);
            }

            @Override
            public String toString() {
                return "ReleaseStatus{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
