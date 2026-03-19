package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediumResource extends AbstractAPIObject {
    private final int mediumNumber;
    private final String mediumName;
    private final String mediumFormat;

    public MediumResource(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.mediumNumber = NumberParser.getInt(-1, obj, "mediumNumber");
        this.mediumName = StringParser.get(obj, "mediumName");
        this.mediumFormat = StringParser.get(obj, "mediumFormat");
    }

    public int mediumNumber() {
        return mediumNumber;
    }

    public @Nullable String mediumName() {
        return mediumName;
    }

    public @Nullable String mediumFormat() {
        return mediumFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediumResource that)) return false;
        return mediumNumber == that.mediumNumber && Objects.equals(mediumName, that.mediumName) && Objects.equals(mediumFormat, that.mediumFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediumNumber, mediumName, mediumFormat);
    }

    @Override
    public String toString() {
        return "MediumResource{" +
                "mediumNumber=" + mediumNumber +
                ", mediumName='" + mediumName + '\'' +
                ", mediumFormat='" + mediumFormat + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
