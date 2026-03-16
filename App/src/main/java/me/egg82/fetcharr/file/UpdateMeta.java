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

public class UpdateMeta {
    private final Logger logger = LoggerFactory.getLogger(UpdateMeta.class);

    private final JSONFile file;
    private Instant last;

    public UpdateMeta(@NotNull JSONFile file) {
        this.file = file;
        read();
    }

    private void read() {
        try {
            JSONObject obj = file.read().getObject();
            if (obj == null || obj.isEmpty()) {
                this.last = Instant.EPOCH;
                return;
            }

            this.last = InstantParser.parse(Instant.EPOCH, obj.getString("last"));
        } catch (Exception ex) {
            logger.warn("Could not read meta from {}: ", file.path(), ex);

            this.last = Instant.EPOCH;
        }
    }

    public void write() {
        JSONObject obj = new JSONObject(Map.of(
                "last", last.toString()
        ));

        try {
            file.write(new JsonNode(obj.toString()));
        } catch (IOException ex) {
            logger.warn("Could not write meta to {}: ", file.path(), ex);
        }
    }

    public @NotNull Instant last() {
        return last;
    }

    public void last(@NotNull Instant last) {
        this.last = last;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdateMeta that)) return false;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "UpdateMeta{" +
                "file=" + file +
                ", last=" + last +
                '}';
    }
}
