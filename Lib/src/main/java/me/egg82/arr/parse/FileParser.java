package me.egg82.arr.parse;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.InvalidPathException;

public class FileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);

    public static @NotNull File get(@NotNull File def, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, obj, key, false);
    }

    public static @NotNull File get(@NotNull File def, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        File r = get(obj, key, silent);
        return r != null ? r : def;
    }

    public static @NotNull File parse(@NotNull File def, @Nullable String val) {
        return parse(def, val, false);
    }

    public static @NotNull File parse(@NotNull File def, @Nullable String val, boolean silent) {
        File r = parse(val, silent);
        return r != null ? r : def;
    }

    public static @Nullable File get(@Nullable JSONObject obj, @Nullable String key) {
        return get(obj, key, false);
    }

    public static @Nullable File get(@Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key), silent);
    }

    public static @Nullable File parse(@Nullable String val) {
        return parse(val, false);
    }

    public static @Nullable File parse(@Nullable String val, boolean silent) {
        if (val == null || val.isBlank()) {
            return null;
        }

        val = val.trim();
        File r = new File(val);
        try {
            r.toPath();
            return r;
        } catch (InvalidPathException ex) {
            if (!silent) {
                LOGGER.warn("Could not parse file from string value \"{}\"", val, ex);
            }
            return null;
        }
    }

    private FileParser() { }
}
