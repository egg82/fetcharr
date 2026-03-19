package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IsoCountry extends AbstractAPIObject {
    private final String twoLetterCode;
    private final String name;

    public IsoCountry(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.twoLetterCode = StringParser.get(obj, "twoLetterCode");
        this.name = StringParser.get(obj, "name");
    }

    public @Nullable String twoLetterCode() {
        return twoLetterCode;
    }

    public @Nullable String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IsoCountry that)) return false;
        return Objects.equals(twoLetterCode, that.twoLetterCode) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(twoLetterCode, name);
    }

    @Override
    public String toString() {
        return "IsoCountry{" +
                "twoLetterCode='" + twoLetterCode + '\'' +
                ", name='" + name + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
