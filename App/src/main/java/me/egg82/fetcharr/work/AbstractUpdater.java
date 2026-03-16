package me.egg82.fetcharr.work;

import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.file.UpdateMeta;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;

public abstract class AbstractUpdater implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ArrAPI api;
    protected final UpdateMeta meta;
    protected Instant lastUpdate;

    public AbstractUpdater(@NotNull ArrAPI api) {
        this.api = api;
        this.meta = new UpdateMeta(new JSONFile(new File(getBasePath(), "base.meta.json")));
        this.lastUpdate = meta.last();
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
}
