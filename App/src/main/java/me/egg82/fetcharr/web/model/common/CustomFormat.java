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

public class CustomFormat extends AbstractAPIObject<CustomFormat> {
    public static final CustomFormat UNKNOWN = new CustomFormat(ArrAPI.UNKNOWN, -1);

    private final int id;

    private boolean includeCustomFormatWhenRenaming;
    private String name = null;
    private final Set<@NotNull CustomFormatSpecificationSchema> specifications = new HashSet<>();

    public CustomFormat(@NotNull ArrAPI api, int id) {
        super(api, "/api/" + api.version() + "/customformat/" + id);
        this.id = id;
    }

    @Override
    public CustomFormat fetch(@NotNull String apiKey) {
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

        this.includeCustomFormatWhenRenaming = BooleanParser.parse(false, StringParser.parse(obj, "includeCustomFormatWhenRenaming"));

        this.specifications.clear();
        JSONArray specifications = obj.has("specifications") ? obj.getJSONArray("specifications") : null;
        if (specifications != null) {
            for (int i = 0; i < specifications.length(); i++) {
                this.specifications.add(new CustomFormatSpecificationSchema(specifications.getJSONObject(i)));
            }
        }

        this.name = StringParser.parse(obj, "name");
    }

    public int id() {
        return id;
    }

    public boolean includeCustomFormatWhenRenaming() {
        return includeCustomFormatWhenRenaming;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull Set<@NotNull CustomFormatSpecificationSchema> specifications() {
        return specifications;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomFormat that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CustomFormat{" +
                "id=" + id +
                ", includeCustomFormatWhenRenaming=" + includeCustomFormatWhenRenaming +
                ", name='" + name + '\'' +
                ", specifications=" + specifications +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class CustomFormatSpecificationSchema {
        private final int id;

        private final Set<@NotNull Field> fields = new HashSet<>();
        private final String implementation;
        private final String implementationName;
        private final String infoLink;
        private final String name;
        private final boolean negate;
        private final Set<@NotNull CustomFormatSpecificationSchema> presets = new HashSet<>();
        private final boolean required;

        public CustomFormatSpecificationSchema(@NotNull JSONObject obj) {
            this.id = NumberParser.parseInt(-1, StringParser.parse(obj, "id"));

            JSONArray fields = obj.has("fields") ? obj.getJSONArray("fields") : null;
            if (fields != null) {
                for (int i = 0; i < fields.length(); i++) {
                    this.fields.add(new Field(fields.getJSONObject(i)));
                }
            }

            this.implementation = StringParser.parse(obj, "implementation");
            this.implementationName = StringParser.parse(obj, "implementationName");
            this.infoLink = StringParser.parse(obj, "infoLink");
            this.name = StringParser.parse(obj, "name");
            this.negate = BooleanParser.parse(false, StringParser.parse(obj, "negate"));

            JSONArray presets = obj.has("presets") ? obj.getJSONArray("presets") : null;
            if (presets != null) {
                for (int i = 0; i < presets.length(); i++) {
                    this.presets.add(new CustomFormatSpecificationSchema(presets.getJSONObject(i)));
                }
            }

            this.required = BooleanParser.parse(false, StringParser.parse(obj, "required"));
        }

        public int id() {
            return id;
        }

        public @NotNull Set<@NotNull Field> fields() {
            return fields;
        }

        public @Nullable String implementation() {
            return implementation;
        }

        public @Nullable String implementationName() {
            return implementationName;
        }

        public @Nullable String infoLink() {
            return infoLink;
        }

        public @Nullable String name() {
            return name;
        }

        public boolean negate() {
            return negate;
        }

        public @NotNull Set<@NotNull CustomFormatSpecificationSchema> presets() {
            return presets;
        }

        public boolean required() {
            return required;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CustomFormatSpecificationSchema that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "CustomFormatSpecificationSchema{" +
                    "id=" + id +
                    ", fields=" + fields +
                    ", implementation='" + implementation + '\'' +
                    ", implementationName='" + implementationName + '\'' +
                    ", infoLink='" + infoLink + '\'' +
                    ", name='" + name + '\'' +
                    ", negate=" + negate +
                    ", presets=" + presets +
                    ", required=" + required +
                    '}';
        }
    }
}
