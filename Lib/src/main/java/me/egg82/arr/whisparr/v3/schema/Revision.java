package me.egg82.arr.whisparr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import org.jetbrains.annotations.NotNull;

public class Revision extends AbstractAPIObject {
    private final int version;
    private final int real;
    private final boolean isRepack;

    public Revision(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.version = NumberParser.getInt(-1, obj, "version");
        this.real = NumberParser.getInt(-1, obj, "real");
        this.isRepack = BooleanParser.get(false, obj, "isRepack");
    }

    public int version() {
        return version;
    }

    public int real() {
        return real;
    }

    public boolean isRepack() {
        return isRepack;
    }

    @Override
    public String toString() {
        return "Revision{" +
                "version=" + version +
                ", real=" + real +
                ", isRepack=" + isRepack +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
