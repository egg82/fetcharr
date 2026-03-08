package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.BooleanParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class QualityModel {
    private final Quality quality;
    private final Revision revision;

    public QualityModel(@NotNull JSONObject obj) {
        this.quality = obj.has("quality") ? new Quality(obj.getJSONObject("quality")) : null;
        this.revision = obj.has("revision") ? new Revision(obj.getJSONObject("revision")) : null;
    }

    public @Nullable Quality quality() {
        return quality;
    }

    public @Nullable Revision revision() {
        return revision;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityModel that)) return false;
        return Objects.equals(quality, that.quality) && Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, revision);
    }

    @Override
    public String toString() {
        return "QualityModel{" +
                "quality=" + quality +
                ", revision=" + revision +
                '}';
    }

    public static class Revision {
        private final boolean isRepack;
        private final int real;
        private final int version;

        public Revision(@NotNull JSONObject obj) {
            this.isRepack = BooleanParser.parse(false, StringParser.parse(obj, "isRepack"));
            this.real = NumberParser.parseInt(-1, StringParser.parse(obj, "real"));
            this.version = NumberParser.parseInt(-1, StringParser.parse(obj, "version"));
        }

        public boolean isRepack() {
            return isRepack;
        }

        public int real() {
            return real;
        }

        public int version() {
            return version;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Revision revision)) return false;
            return real == revision.real && version == revision.version;
        }

        @Override
        public int hashCode() {
            return Objects.hash(real, version);
        }

        @Override
        public String toString() {
            return "Revision{" +
                    "isRepack=" + isRepack +
                    ", real=" + real +
                    ", version=" + version +
                    '}';
        }
    }
}
