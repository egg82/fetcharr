package me.egg82.arr.lidarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.DownloadClient;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexerResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final List<@NotNull Field> fields = new ArrayList<>();
    private final String implementationName;
    private final String implementation;
    private final String configContract;
    private final String infoLink;
    private final ProviderMessage message;
    private final IntList tags = new IntArrayList();
    private final List<@NotNull Object> presets = new ArrayList<>();
    private final boolean enableRss;
    private final boolean enableAutomaticSearch;
    private final boolean enableInteractiveSearch;
    private final boolean supportsRss;
    private final boolean supportsSearch;
    private final DownloadProtocol protocol;
    private final int priority;
    private final int downloadClientId;

    public IndexerResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");

        JSONArray fields = obj.has("fields") && obj.get("fields") != null ? obj.getJSONArray("fields") : null;
        if (fields != null) {
            for (int i = 0; i < fields.length(); i++) {
                this.fields.add(new Field(api, fields.getJSONObject(i)));
            }
        }

        this.implementationName = StringParser.get(obj, "implementationName");
        this.implementation = StringParser.get(obj, "implementation");
        this.configContract = StringParser.get(obj, "configContract");
        this.infoLink = StringParser.get(obj, "infoLink");
        this.message = ObjectParser.get(ProviderMessage.class, api, obj, "message");
        this.enableRss = BooleanParser.get(false, obj, "enableRss");
        this.enableAutomaticSearch = BooleanParser.get(false, obj, "enableAutomaticSearch");
        this.enableInteractiveSearch = BooleanParser.get(false, obj, "enableInteractiveSearch");
        this.supportsRss = BooleanParser.get(false, obj, "supportsRss");
        this.supportsSearch = BooleanParser.get(false, obj, "supportsSearch");
        this.protocol = DownloadProtocol.get(DownloadProtocol.UNKNOWN, obj, "protocol");
        this.priority = NumberParser.getInt(-1, obj, "priority");
        this.downloadClientId = NumberParser.getInt(-1, obj, "downloadClientId");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull List<@NotNull Field> fields() {
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

    public @NotNull ProviderMessage message() {
        return message;
    }

    public @NotNull List<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return r;
    }

    public @NotNull List<@NotNull Object> presets() {
        return presets;
    }

    public boolean enableRss() {
        return enableRss;
    }

    public boolean enableAutomaticSearch() {
        return enableAutomaticSearch;
    }

    public boolean enableInteractiveSearch() {
        return enableInteractiveSearch;
    }

    public boolean supportsRss() {
        return supportsRss;
    }

    public boolean supportsSearch() {
        return supportsSearch;
    }

    public @NotNull DownloadProtocol protocol() {
        return protocol;
    }

    public int priority() {
        return priority;
    }

    public @Nullable DownloadClient downloadClient() {
        return api.fetch(DownloadClient.class, downloadClientId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IndexerResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "IndexerResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fields=" + fields +
                ", implementationName='" + implementationName + '\'' +
                ", implementation='" + implementation + '\'' +
                ", configContract='" + configContract + '\'' +
                ", infoLink='" + infoLink + '\'' +
                ", message=" + message +
                ", tags=" + tags +
                ", presets=" + presets +
                ", enableRss=" + enableRss +
                ", enableAutomaticSearch=" + enableAutomaticSearch +
                ", enableInteractiveSearch=" + enableInteractiveSearch +
                ", supportsRss=" + supportsRss +
                ", supportsSearch=" + supportsSearch +
                ", protocol=" + protocol +
                ", priority=" + priority +
                ", downloadClientId=" + downloadClientId +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
