package me.egg82.fetcharr.file;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.InstantParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class CacheMeta {
    private final Logger logger = LoggerFactory.getLogger(CacheMeta.class);

    private final JSONFile file;
    private Instant fetched;
    private Instant selected;

    public CacheMeta(@NotNull JSONFile file) {
        this.file = file;
        read();
    }

    private void read() {
        try {
            JSONObject obj = file.read().getObject();
            if (obj == null || obj.isEmpty()) {
                this.fetched = Instant.now();
                this.selected = Instant.EPOCH;
                return;
            }

            this.fetched = InstantParser.parse(Instant.now(), obj.getString("fetched"));
            this.selected = InstantParser.parse(Instant.EPOCH, obj.getString("selected"));
        } catch (Exception ex) {
            logger.warn("Could not read meta from {}: ", file.path(), ex);

            this.fetched = Instant.now();
            this.selected = Instant.EPOCH;
        }
    }

    public void write() {
        JSONObject obj = new JSONObject(Map.of(
                "fetched", fetched.toString(),
                "selected", selected.toString()
        ));

        try {
            file.write(new JsonNode(obj.toString()));
        } catch (IOException ex) {
            logger.warn("Could not write meta to {}: ", file.path(), ex);
        }
    }

    public @NotNull Instant fetched() {
        return fetched;
    }

    public void setFetched(@NotNull Instant fetched) {
        this.fetched = fetched;
    }

    public @NotNull Instant selected() {
        return selected;
    }

    public void setSelected(@NotNull Instant selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CacheMeta cacheMeta)) return false;
        return Objects.equals(file, cacheMeta.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "CacheMeta{" +
                "file=" + file +
                ", fetched=" + fetched +
                ", selected=" + selected +
                '}';
    }
}
