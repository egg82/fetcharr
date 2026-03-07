package me.egg82.fetcharr.parse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.InvalidPathException;

public class FileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);

    public static @NotNull File parse(@NotNull File def, @Nullable String val) {
        File r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable File parse(@Nullable String val) {
        if (val == null) {
            return null;
        }

        File r = new File(val);
        try {
            r.toPath();
            return r;
        } catch (InvalidPathException ex) {
            LOGGER.warn("Could not parse file from string value \"{}\"", val, ex);
            return null;
        }
    }

    private FileParser() { }
}
