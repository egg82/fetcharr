package me.egg82.arr.file;

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

public class FetchableAPIObjectMeta {
    private final Logger logger = LoggerFactory.getLogger(FetchableAPIObjectMeta.class);

    private final JSONFile file;
    private Instant lastFetched;

    public FetchableAPIObjectMeta(@NotNull JSONFile file) {
        this.file = file;
        read();
    }

    private void read() {
        try {
            JSONObject obj = file.read().getObject();
            if (obj == null || obj.isEmpty()) {
                this.lastFetched = Instant.now();
                return;
            }

            this.lastFetched = InstantParser.parse(Instant.now(), obj.getString("lastFetched"));
        } catch (Exception ex) {
            logger.warn("Could not read meta from {}: ", file.path(), ex);

            this.lastFetched = Instant.now();
        }
    }

    public void write() {
        JSONObject obj = new JSONObject(Map.of(
                "lastFetched", lastFetched.toString()
        ));

        try {
            file.write(new JsonNode(obj.toString()));
        } catch (IOException ex) {
            logger.warn("Could not write meta to {}: ", file.path(), ex);
        }
    }

    public @NotNull Instant lastFetched() {
        return lastFetched;
    }

    public void setFetched(@NotNull Instant lastFetched) {
        this.lastFetched = lastFetched;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FetchableAPIObjectMeta fetchableApiObjectMeta)) return false;
        return Objects.equals(file, fetchableApiObjectMeta.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "FetchableAPIObjectMeta{" +
                "file=" + file +
                ", lastFetched=" + lastFetched +
                '}';
    }
}
