package me.egg82.fetcharr.file;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.file.JSONFile;import me.egg82.arr.parse.InstantParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class UpdaterMeta {
    private final Logger logger = LoggerFactory.getLogger(UpdaterMeta.class);

    private final JSONFile file;
    private Instant lastUpdate;

    public UpdaterMeta(@NotNull JSONFile file) {
        this.file = file;
        read();
    }

    private void read() {
        try {
            JSONObject obj = file.read().getObject();
            if (obj == null || obj.isEmpty()) {
                this.lastUpdate = Instant.EPOCH;
                return;
            }

            this.lastUpdate = InstantParser.parse(Instant.EPOCH, obj.getString("lastUpdate"));
        } catch (Exception ex) {
            logger.warn("Could not read meta from {}: ", file.path(), ex);

            this.lastUpdate = Instant.EPOCH;
        }
    }

    public void write() {
        JSONObject obj = new JSONObject(Map.of(
                "lastUpdate", lastUpdate.toString()
        ));

        try {
            file.write(new JsonNode(obj.toString()));
        } catch (IOException ex) {
            logger.warn("Could not write meta to {}: ", file.path(), ex);
        }
    }

    public @NotNull Instant lastUpdate() {
        return lastUpdate;
    }

    public void lastUpdate(@NotNull Instant last) {
        this.lastUpdate = last;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdaterMeta that)) return false;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public String toString() {
        return "UpdaterMeta{" +
                "file=" + file +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
