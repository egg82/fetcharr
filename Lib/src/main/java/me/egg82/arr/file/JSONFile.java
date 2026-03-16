package me.egg82.arr.file;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class JSONFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONFile.class);

    private final File file;

    public JSONFile(@NotNull File file) {
        this.file = file;
    }

    public @NotNull String absolutePath() {
        return file.getAbsolutePath();
    }

    public @NotNull JsonNode read() throws IOException {
        JsonNode obj = readNode();
        if (obj == null) {
            obj = new JsonNode(new JSONObject().toString());
        }
        return obj;
    }

    public void delete() throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists() || !parent.isDirectory()) {
            return;
        }

        if (file.exists()) {
            if (file.isDirectory()) {
                try (Stream<Path> files = Files.walk(file.toPath())) {
                    files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
            } else {
                Files.delete(file.toPath());
            }
        }
    }

    public boolean exists() {
        return file.exists() && file.isFile();
    }

    private @Nullable JsonNode readNode() throws IOException {
        if (!file.exists() || (file.exists() && file.isDirectory())) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(file); BufferedReader in = new BufferedReader(reader)) {
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
        }
        return new JsonNode(builder.toString().trim());
    }

    public void write(@NotNull JsonNode data) throws IOException {
        File parent = file.getParentFile();
        if (parent.exists() && !parent.isDirectory()) {
            Files.delete(parent.toPath());
        }
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create parent directory structure.");
        }

        if (file.exists() && file.isDirectory()) {
            try (Stream<Path> files = Files.walk(file.toPath())) {
                files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create parent directory structure.");
        }

        try (FileWriter out = new FileWriter(file)) {
            if (data.isArray()) {
                data.getArray().write(out);
            } else {
                data.getObject().write(out);
            }
        }

        LOGGER.trace("Wrote {} bytes to {}", file.length(), file.getAbsolutePath());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JSONFile jsonFile)) return false;
        return Objects.equals(file, jsonFile.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "JsonFile{" +
                "file=" + file +
                '}';
    }
}
