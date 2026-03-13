package me.egg82.fetcharr.work;

import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.file.JSONFile;
import me.egg82.fetcharr.file.UpdateMeta;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.model.common.Tag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.util.Collection;

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
        File base = ConfigVars.getFile(ConfigVars.CACHE_DIR);
        File arr = new File(base, api.type().name().toLowerCase() + "-" + api.id());
        return new File(arr, getClass().getSimpleName());
    }

    protected final boolean hasAnyTag(@NotNull String @NotNull [] needles, @NotNull Collection<@NotNull Tag> haystack) {
        if (needles.length == 0 || haystack.isEmpty()) {
            return false;
        }

        for (String n : needles) {
            for (Tag t : haystack) {
                String label = t.label();
                if (label != null && label.equalsIgnoreCase(n)) {
                    return true;
                }
            }
        }
        return false;
    }
}
