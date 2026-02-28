package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.ParsedDateTime;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class APIObject {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final JSONObject obj;
    protected final ArrAPI api;

    protected JSONFile file = null;
    protected JSONFile metaFile = null;

    protected APIObject(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        this.obj = obj;
        this.api = api;
    }

    public static @NotNull File getPath(@NotNull ArrAPI api, @NotNull Class<? extends APIObject> clazz, int id) {
        return new File(getBasePath(api, clazz), id + ".json");
    }

    public static @NotNull File getMetaPath(@NotNull ArrAPI api, @NotNull Class<? extends APIObject> clazz, int id) {
        return new File(getBasePath(api, clazz), id + ".meta.json");
    }

    private static @NotNull File getBasePath(@NotNull ArrAPI api, @NotNull Class<? extends APIObject> clazz) {
        File base = ConfigVars.getVar(ConfigVars.DATA_DIR, (File) ConfigVars.DATA_DIR.def());

        File arr;
        if (api instanceof RadarrAPI) {
            arr = new File(base, "radarr-" + api.id());
        } else {
            arr = new File(base, "unknown");
        }

        return new File(arr, clazz.getSimpleName());
    }

    public abstract @NotNull APIMeta meta();

    protected @NotNull JSONObject traverseObj(@NotNull JSONObject o, @NotNull String... path) {
        if (path.length <= 1) {
            return o;
        }

        for (int i = 0; i < path.length - 1; i++) {
            try {
                o = o.getJSONObject(path[i]);
            } catch (JSONException ignored) {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", Arrays.copyOfRange(path, 0, i + 1)));
            }
        }
        return o;
    }

    protected int getInt(int def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse empty JSON path");
            return def;
        }

        try {
            JSONObject o = traverseObj(obj);
            if (o.has(path[path.length - 1])) {
                return o.getInt(path[path.length - 1]);
            } else {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", path));
            }
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to int", String.join(".", path), ex);
        }
        return def;
    }

    protected @NotNull Set<@NotNull Integer> getIntSet(@NotNull Set<@NotNull Integer> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<Integer> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                v.add(arr.getInt(i));
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to int", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    protected long getLong(long def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse empty JSON path");
            return def;
        }

        try {
            JSONObject o = traverseObj(obj);
            if (o.has(path[path.length - 1])) {
                return o.getLong(path[path.length - 1]);
            } else {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", path));
            }
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to long", String.join(".", path), ex);
        }
        return def;
    }

    protected float getFloat(float def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse empty JSON path");
            return def;
        }

        try {
            JSONObject o = traverseObj(obj);
            if (o.has(path[path.length - 1])) {
                return o.getFloat(path[path.length - 1]);
            } else {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", path));
            }
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to float", String.join(".", path), ex);
        }
        return def;
    }

    protected boolean getBoolean(boolean def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse empty JSON path");
            return def;
        }

        try {
            JSONObject o = traverseObj(obj);
            if (o.has(path[path.length - 1])) {
                return o.getBoolean(path[path.length - 1]);
            } else {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", path));
            }
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to boolean", String.join(".", path), ex);
        }
        return def;
    }

    protected @NotNull String getString(@NotNull String def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse empty JSON path");
            return def;
        }

        try {
            JSONObject o = traverseObj(obj);
            if (o.has(path[path.length - 1])) {
                return o.getString(path[path.length - 1]);
            } else {
                logger.debug("Could not traverse JSON path {} (failed at {})", String.join(".", path), String.join(".", path));
            }
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to string", String.join(".", path), ex);
        }
        return def;
    }

    protected @NotNull Set<@NotNull String> getStringSet(@NotNull Set<@NotNull String> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<String> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                v.add(arr.getString(i));
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to string", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    protected @NotNull ReleaseStatus getReleaseStatus(@NotNull ReleaseStatus def, @NotNull String... path) {
        String val = getString("", path);
        if (val.isEmpty()) {
            return def;
        }

        ReleaseStatus v = ReleaseStatus.fromString(val);
        if  (v == null) {
            logger.warn("Could not transform {} to release status. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull ParsedDateTime getDateTime(@NotNull ParsedDateTime def, @NotNull String... path) {
        String val = getString("", path);
        if (val.isEmpty()) {
            return def;
        }

        ParsedDateTime v = ParsedDateTime.fromString(val);
        if  (v == null) {
            logger.warn("Could not transform {} to date/time. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull QualityProfile getQualityProfile(@NotNull QualityProfile def, @NotNull String... path) {
        int val = getInt(-1, path);
        if (val < 0) {
            return def;
        }

        QualityProfile v = api.qualityProfile(val);
        if  (v == null) {
            logger.warn("Could not transform {} to quality profile. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull Language getLanguage(@NotNull Language def, @NotNull String... path) {
        int val = getInt(-1, path);
        if (val < 0) {
            return def;
        }

        Language v = api.language(val);
        if  (v == null) {
            logger.warn("Could not transform {} to language. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull Set<@NotNull Language> getLanguageSet(@NotNull Set<@NotNull Language> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<Language> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                Language l = api.language(arr.getJSONObject(i).getInt("id"));
                if (l != null) {
                    v.add(l);
                }
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to language", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    protected @NotNull QualityProfile.Quality getQuality(@NotNull QualityProfile.Quality def, @NotNull String... path) {
        if (path.length == 0) {
            logger.warn("Could not traverse JSON path {}", String.join(".", path));
            return def;
        }

        try {
            return new QualityProfile.Quality(traverseObj(obj).getJSONObject(path[path.length - 1]), api);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to int", String.join(".", path), ex);
        }
        return def;
    }

    protected @NotNull CustomFormat getCustomFormat(@NotNull CustomFormat def, @NotNull String... path) {
        int val = getInt(-1, path);
        if (val < 0) {
            return def;
        }

        CustomFormat v = api.customFormat(val);
        if  (v == null) {
            logger.warn("Could not transform {} to custom format. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull Set<@NotNull CustomFormat> getCustomFormatSet(@NotNull Set<@NotNull CustomFormat> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<CustomFormat> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                CustomFormat f = api.customFormat(arr.getJSONObject(i).getInt("id"));
                if (f != null) {
                    v.add(f);
                }
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to custom fomat", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }

    protected @NotNull Tag getTag(@NotNull Tag def, @NotNull String... path) {
        int val = getInt(-1, path);
        if (val < 0) {
            return def;
        }

        Tag v = api.tag(val);
        if  (v == null) {
            logger.warn("Could not transform {} to tag. Got unexpected \"{}\"", String.join(".", path), val);
            return def;
        }
        return v;
    }

    protected @NotNull Set<@NotNull Tag> getTagSet(@NotNull Set<@NotNull Tag> def, @NotNull String... path) {
        JSONArray arr;
        try {
            arr = traverseObj(obj).getJSONArray(path[path.length - 1]);
        } catch (JSONException ex) {
            logger.warn("Could not transform {} to array", String.join(".", path), ex);
            return def;
        }
        Set<Tag> v = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                Tag f = api.tag(arr.getInt(i));
                if (f != null) {
                    v.add(f);
                }
            } catch (JSONException ex) {
                logger.warn("Could not transform {} at index {} to tag", String.join(".", path), i, ex);
            }
        }
        return !v.isEmpty() ? v : def;
    }
}
