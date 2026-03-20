package me.egg82.arr.lidarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.lidarr.v1.Tag;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadClientResource extends AbstractAPIObject {
    private final int id;
    private final String name;
    private final PVector<@NotNull Field> fields;
    private final String implementationName;
    private final String implementation;
    private final String configContract;
    private final String infoLink;
    private final ProviderMessage message;
    private final IntSet tags = new IntArraySet();
    private final PVector<@NotNull Object> presets;
    private final boolean enable;
    private final DownloadProtocol protocol;
    private final int priority;
    private final boolean removeCompletedDownloads;
    private final boolean removeFailedDownloads;

    public DownloadClientResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.id = NumberParser.getInt(-1, obj, "id");
        this.name = StringParser.get(obj, "name");

        JSONArray fields = obj.has("fields") && obj.get("fields") != null ? obj.getJSONArray("fields") : null;
        List<@NotNull Field> fieldsL = new ArrayList<>();
        if (fields != null) {
            for (int i = 0; i < fields.length(); i++) {
                fieldsL.add(new Field(api, fields.getJSONObject(i)));
            }
        }
        this.fields = TreePVector.from(fieldsL);

        this.implementationName = StringParser.get(obj, "implementationName");
        this.implementation = StringParser.get(obj, "implementation");
        this.configContract = StringParser.get(obj, "configContract");
        this.infoLink = StringParser.get(obj, "infoLink");

        this.message = ObjectParser.get(ProviderMessage.class, api, obj, "message");

        JSONArray tags = obj.has("tags") && obj.get("tags") != null ? obj.getJSONArray("tags") : null;
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                this.tags.add(tags.getInt(i));
            }
        }

        JSONArray presets = obj.has("presets") && obj.get("presets") != null ? obj.getJSONArray("presets") : null;
        List<@NotNull Object> presetsL = new ArrayList<>();
        if (presets != null) {
            for (int i = 0; i < presets.length(); i++) {
                presetsL.add(presets.get(i));
            }
        }
        this.presets = TreePVector.from(presetsL);

        this.enable = BooleanParser.get(false, obj, "enable");
        this.protocol = DownloadProtocol.get(DownloadProtocol.UNKNOWN, obj, "protocol");
        this.priority = NumberParser.getInt(-1, obj, "priority");
        this.removeCompletedDownloads = BooleanParser.get(false, obj, "removeCompletedDownloads");
        this.removeFailedDownloads = BooleanParser.get(false, obj, "removeFailedDownloads");
    }

    public int id() {
        return id;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull PVector<@NotNull Field> fields() {
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

    public @NotNull PVector<@NotNull Tag> tags() {
        List<@NotNull Tag> r = new ArrayList<>();
        for (int id : this.tags) {
            Tag t = api.fetch(Tag.class, id);
            if (t != null) {
                r.add(t);
            }
        }
        return TreePVector.from(r);
    }

    public @NotNull PVector<@NotNull Object> presets() {
        return presets;
    }

    public boolean enable() {
        return enable;
    }

    public @NotNull DownloadProtocol protocol() {
        return protocol;
    }

    public int priority() {
        return priority;
    }

    public boolean removeCompletedDownloads() {
        return removeCompletedDownloads;
    }

    public boolean removeFailedDownloads() {
        return removeFailedDownloads;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DownloadClientResource that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DownloadClientResource{" +
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
                ", enable=" + enable +
                ", protocol=" + protocol +
                ", priority=" + priority +
                ", removeCompletedDownloads=" + removeCompletedDownloads +
                ", removeFailedDownloads=" + removeFailedDownloads +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
