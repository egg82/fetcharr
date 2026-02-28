package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class Tag extends APIObject {
    public static final Tag UNKNOWN = new Tag();

    private final int id;
    private final String label;

    public Tag(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);

        this.id = getInt(-1, "id");
        this.label = getString("", "label");

        this.file = new JSONFile(getPath(api, getClass(), id));
        this.metaFile = new JSONFile(getMetaPath(api, getClass(), id));
        if (!file.exists()) {
            try {
                this.file.write(obj);
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
        if (!metaFile.exists()) {
            try {
                this.metaFile.write(meta().object());
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
    }

    private Tag() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.id = -1;
        this.label = "";

        this.file = new JSONFile(getPath(api, getClass(), id));
        this.metaFile = new JSONFile(getMetaPath(api, getClass(), id));
        if (!file.exists()) {
            try {
                this.file.write(obj);
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
        if (!metaFile.exists()) {
            try {
                this.metaFile.write(meta().object());
            } catch (IOException ex) {
                logger.warn("Could not write JSON data to {}", this.file.path());
            }
        }
    }

    public boolean unknown() { return id < 0; }

    @Override
    public @NotNull APIMeta meta() {
        return new APIMeta();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag tag)) return false;
        return id == tag.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }

    public int id() {
        return id;
    }

    public @NotNull String label() {
        return label;
    }
}
