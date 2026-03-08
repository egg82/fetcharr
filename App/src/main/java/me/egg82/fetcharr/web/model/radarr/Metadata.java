package me.egg82.fetcharr.web.model.radarr;

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
import me.egg82.fetcharr.web.model.common.Field;
import me.egg82.fetcharr.web.model.common.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Metadata extends AbstractAPIObject<Metadata> {
    public static Metadata UNKNOWN = new Metadata(ArrAPI.UNKNOWN, -1);

    private final int id;

    private String name;
    private final Set<@NotNull Field> fields = new HashSet<>();
    private String implementationName;
    private String implementation;
    private String configContract;
    private String infoLink;
    private ProviderMessage message;
    private final Set<@NotNull Tag> tags = new HashSet<>();
    private boolean enable;

    public Metadata(@NotNull ArrAPI api, int id) {
        super(api, "/api/v3/metadata/" + id);
        this.id = id;
    }

    @Override
    public Metadata fetch(@NotNull String apiKey) {
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

        this.fields.clear();
        JSONArray fields = obj.has("fields") ? obj.getJSONArray("fields") : null;
        if (fields != null) {
            for (int i = 0; i < fields.length(); i++) {
                this.fields.add(new Field(fields.getJSONObject(i)));
            }
        }

        this.implementationName = StringParser.parse(obj, "implementationName");
        this.implementation = StringParser.parse(obj, "implementation");
        this.configContract = StringParser.parse(obj, "configContract");
        this.infoLink = StringParser.parse(obj, "infoLink");
        this.message = obj.has("message") ? new ProviderMessage(obj.getJSONObject("message")) : null;

        this.tags.clear();
        JSONArray tags = obj.has("tags") ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                int id = NumberParser.parseInt(-1, tags.getString(i));
                if (id >= 0) {
                    this.tags.add(api.fetch(Tag.class, id, false));
                }
            }
        }

        this.enable = BooleanParser.parse(false, StringParser.parse(obj, "enable"));

        this.name = StringParser.parse(obj, "name");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull Set<@NotNull Field> fields() {
        return fields;
    }

    public @Nullable String implementationName() {
        return implementationName;
    }

    public @Nullable String implementation() {
        return implementation;
    }

    public @Nullable String configContract() {
        return configContract;
    }

    public @Nullable String infoLink() {
        return infoLink;
    }

    public @Nullable ProviderMessage message() {
        return message;
    }

    public @NotNull Set<@NotNull Tag> tags() {
        return tags;
    }

    public boolean enable() {
        return enable;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Metadata metadata)) return false;
        return id == metadata.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fields=" + fields +
                ", implementationName='" + implementationName + '\'' +
                ", implementation='" + implementation + '\'' +
                ", configContract='" + configContract + '\'' +
                ", infoLink='" + infoLink + '\'' +
                ", message=" + message +
                ", tags=" + tags +
                ", enable=" + enable +
                ", api=" + api +
                ", apiPath='" + apiPath + '\'' +
                ", fetched=" + fetched +
                '}';
    }

    public static class ProviderMessage {
        private final String message;
        private final ProviderMessageType type;

        public ProviderMessage(@NotNull JSONObject obj) {
            this.message = StringParser.parse(obj, "message");
            this.type = ProviderMessageType.parse(ProviderMessageType.INFO, StringParser.parse(obj, "type"));
        }

        public @Nullable String message() {
            return message;
        }

        public @NotNull ProviderMessageType type() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ProviderMessage that)) return false;
            return Objects.equals(message, that.message) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, type);
        }

        @Override
        public String toString() {
            return "ProviderMessage{" +
                    "message='" + message + '\'' +
                    ", type=" + type +
                    '}';
        }
    }
}
