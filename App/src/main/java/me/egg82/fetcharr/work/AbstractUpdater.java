package me.egg82.fetcharr.work;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.file.JSONFile;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.fetcharr.file.UpdaterMeta;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public abstract class AbstractUpdater implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ArrAPI api;
    protected final UpdaterMeta metaFile;
    protected Instant lastUpdate;

    public AbstractUpdater(@NotNull ArrAPI api) {
        this.api = api;
        this.metaFile = new UpdaterMeta(new JSONFile(new File(getBasePath(), "meta.json")));
        this.lastUpdate = metaFile.lastUpdate();
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception ex) {
            logger.warn("Exception in {} doWork method", getClass().getSimpleName(), ex);
        }
    }

    abstract protected void doWork();

    private @NotNull File getBasePath() {
        File base = CacheConfigVars.getFile(CacheConfigVars.CACHE_DIR);
        File arr = new File(base, api.type().name().toLowerCase() + "-" + api.id());
        return new File(arr, getClass().getSimpleName());
    }

    protected final boolean isCacheWritable() {
        JSONFile testFile = new JSONFile(new File(CacheConfigVars.getFile(CacheConfigVars.CACHE_DIR), "touch.json"));
        try {
            boolean writable = BooleanParser.get(false, testFile.read().getObject(), "writable");
            if (!writable) {
                testFile.write(new JsonNode(new JSONObject(Map.of("writable", true)).toString()));
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}
