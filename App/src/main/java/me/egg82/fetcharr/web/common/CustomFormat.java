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

public class CustomFormat extends APIObject {
    public static final CustomFormat UNKNOWN = new CustomFormat();

    private final int id;
    private final String name;
    private final boolean includeWithRename;
    private final Set<@NotNull Specification> specifications;

    public CustomFormat(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);

        this.id = getInt(-1, "id");
        this.name = getString("", "name");
        this.includeWithRename = getBoolean(false, "includeCustomFormatWhenRenaming");
        this.specifications = getSpecificationSet(Set.of(), "specifications");

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

    private CustomFormat() {
        super(new JSONObject(), NullAPI.INSTANCE);

        this.id = -1;
        this.name = "";
        this.includeWithRename = false;
        this.specifications = Set.of();

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

    private @NotNull Set<CustomFormat.@NotNull Specification> getSpecificationSet(@NotNull Set<CustomFormat.@NotNull Specification> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<CustomFormat.Specification> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                v.add(new CustomFormat.Specification(arr.getJSONObject(i), api));
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to specification", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomFormat that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CustomFormat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", includeWithRename=" + includeWithRename +
                ", specifications=" + specifications +
                '}';
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public boolean includeWithRename() {
        return includeWithRename;
    }

    public @NotNull Set<@NotNull Specification> specifications() {
        return specifications;
    }

    public static class Specification extends APIObject {
        public static final CustomFormat.Specification UNKNOWN = new CustomFormat.Specification();

        private final String name;
        private final String implementation;
        private final String implementationName;
        private final boolean negate;
        private final boolean required;

        public Specification(@NotNull JSONObject obj, @NotNull ArrAPI api) {
            super(obj, api);

            this.name = getString("", "name");
            this.implementation = getString("", "implementation");
            this.implementationName = getString("", "implementationName");
            this.negate = getBoolean(false, "negate");
            this.required = getBoolean(false, "required");
        }

        private Specification() {
            super(new JSONObject(), NullAPI.INSTANCE);

            this.name = "";
            this.implementation = "";
            this.implementationName = "";
            this.negate = false;
            this.required = false;
        }

        public boolean unknown() { return api instanceof NullAPI; }

        @Override
        public @NotNull APIMeta meta() {
            return new APIMeta();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Specification that)) return false;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {
            return "Specification{" +
                    "name='" + name + '\'' +
                    ", implementation='" + implementation + '\'' +
                    ", implementationName='" + implementationName + '\'' +
                    ", negate=" + negate +
                    ", required=" + required +
                    '}';
        }

        public @NotNull String name() {
            return name;
        }

        public @NotNull String implementation() {
            return implementation;
        }

        public @NotNull String implementationName() {
            return implementationName;
        }

        public boolean negate() {
            return negate;
        }

        public boolean required() {
            return required;
        }
    }
}
