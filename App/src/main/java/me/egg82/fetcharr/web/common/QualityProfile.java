package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class QualityProfile extends APIObject {
    public static final QualityProfile UNKNOWN = new QualityProfile();

    private final String name;
    private final boolean upgrade;
    private final int cutoff;
    private final Set<@NotNull Quality> qualities;
    private final int minFormatScore;
    private final int formatCutoff;
    private final int formatUpgrade;
    private final Set<@NotNull CustomFormat> formats;
    private final Language language;
    private final int id;

    public QualityProfile(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);

        this.name = getString("", "name");
        this.upgrade = getBoolean(false, "upgradeAllowed");
        this.cutoff = getInt(-1, "cutoff");
        this.qualities = getQualitySet(Set.of(), "items");
        this.minFormatScore = getInt(-1, "minFormatScore");
        this.formatCutoff = getInt(-1, "cutoffFormatScore");
        this.formatUpgrade = getInt(-1, "minUpgradeFormatScore");
        this.formats = getCustomFormatSet(Set.of(), "formatItems");
        this.language = getLanguage(Language.UNKNOWN, "language", "id");
        this.id = getInt(-1, "id");

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

    private QualityProfile() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.name = "";
        this.upgrade = false;
        this.cutoff = -1;
        this.qualities = Set.of();
        this.minFormatScore = -1;
        this.formatCutoff = -1;
        this.formatUpgrade = -1;
        this.formats = Set.of();
        this.language = Language.UNKNOWN;
        this.id = -1;

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

    private @NotNull Set<QualityProfile.@NotNull Quality> getQualitySet(@NotNull Set<QualityProfile.@NotNull Quality> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<QualityProfile.Quality> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                if (arr.getJSONObject(i).has("items")) {
                    JSONArray arr2 = arr.getJSONObject(i).getJSONArray("items");
                    for (int j = 0; j < arr2.length(); j++) {
                        v.add(new QualityProfile.Quality(arr2.getJSONObject(j), api));
                    }
                } else {
                    v.add(new QualityProfile.Quality(arr.getJSONObject(i), api));
                }
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to quality", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QualityProfile that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualityProfile{" +
                "name='" + name + '\'' +
                ", upgrade=" + upgrade +
                ", cutoff=" + cutoff +
                ", qualities=" + qualities +
                ", minFormatScore=" + minFormatScore +
                ", formatCutoff=" + formatCutoff +
                ", formatUpgrade=" + formatUpgrade +
                ", formats=" + formats +
                ", language=" + language +
                ", id=" + id +
                '}';
    }

    public @NotNull String name() {
        return name;
    }

    public boolean upgrade() {
        return upgrade;
    }

    public int cutoff() {
        return cutoff;
    }

    public @NotNull Set<@NotNull Quality> qualities() {
        return qualities;
    }

    public int minFormatScore() {
        return minFormatScore;
    }

    public int formatCutoff() {
        return formatCutoff;
    }

    public int formatUpgrade() {
        return formatUpgrade;
    }

    public @NotNull Set<@NotNull CustomFormat> formats() {
        return formats;
    }

    public @NotNull Language language() {
        return language;
    }

    public int id() {
        return id;
    }

    public static class Quality extends APIObject {
        public static final QualityProfile.Quality UNKNOWN = new QualityProfile.Quality();

        private final int id;
        private final String name;
        private final String source;
        private final int resolution;
        private final String modifier;
        private final Set<@NotNull String> items;
        private final boolean allowed;

        public Quality(@NotNull JSONObject obj, @NotNull ArrAPI api) {
            super(obj, api);

            this.id = getInt(-1, "quality", "id");
            this.name = getString("", "quality", "name");
            this.source = getString("", "quality", "source");
            this.resolution = getInt(-1, "quality", "resolution");
            this.modifier = getString("", "quality", "modifier");
            this.items = getStringSet(Set.of(), "items");
            this.allowed = getBoolean(false, "allowed");
        }

        private Quality() {
            super(new JSONObject(), NullAPI.INSTANCE);

            this.id = -1;
            this.name = "";
            this.source = "";
            this.resolution = -1;
            this.modifier = "";
            this.items = Set.of();
            this.allowed = false;
        }

        public boolean unknown() { return id < 0; }

        @Override
        public @NotNull APIMeta meta() {
            return new APIMeta();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Quality quality)) return false;
            return id == quality.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Quality{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", source='" + source + '\'' +
                    ", resolution=" + resolution +
                    ", modifier='" + modifier + '\'' +
                    ", items=" + items +
                    ", allowed=" + allowed +
                    '}';
        }

        public int id() {
            return id;
        }

        public @NotNull String name() {
            return name;
        }

        public @NotNull String source() {
            return source;
        }

        public int resolution() {
            return resolution;
        }

        public @NotNull String modifier() {
            return modifier;
        }

        public @NotNull Set<@NotNull String> items() {
            return items;
        }

        public boolean allowed() {
            return allowed;
        }
    }
}
