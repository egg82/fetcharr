package me.egg82.arr.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class LogFile {
    private final File file;

    private final Object lock = new Object();

    public LogFile(@NotNull File file) throws IOException {
        this.file = file;

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
    }

    public @NotNull String absolutePath() {
        return file.getAbsolutePath();
    }

    public void log(@Nullable String msg) throws IOException {
        if (msg == null) {
            return;
        }

        synchronized (lock) {
            try (FileWriter out = new FileWriter(file)) {
                out.write(msg + System.lineSeparator());
            }
        }
    }

    public void log(@Nullable String msg, @Nullable Throwable t) throws IOException {
        if (msg == null && t == null) {
            return;
        }

        String stacktrace = null;
        if (t != null) {
            try (StringWriter builder = new StringWriter(); PrintWriter writer = new PrintWriter(builder)) {
                t.printStackTrace(writer);
                stacktrace = builder.toString();
            }
        }

        synchronized (lock) {
            try (FileWriter out = new FileWriter(file)) {
                if (msg != null) {
                    out.write(msg + System.lineSeparator());
                }
                if (stacktrace != null) {
                    out.write(stacktrace);
                }
            }
        }
    }

    public void delete() throws IOException {
        synchronized (lock) {
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
    }

    public boolean exists() {
        synchronized (lock) {
            return file.exists() && file.isFile();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LogFile logFile)) return false;
        return Objects.equals(file, logFile.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "file=" + file +
                '}';
    }
}
