package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Language extends APIObject {
    public static final Language UNKNOWN = new Language();

    private final int id;
    private final String name;

    public Language(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);

        this.id = getInt(-1, "id");
        this.name = getString("", "name");
    }

    private Language() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.id = -1;
        this.name = "";
    }

    public boolean unknown() { return id < 0; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Language language)) return false;
        return id == language.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int id() {
        return id;
    }

    public @NotNull String name() {
        return name;
    }
}
