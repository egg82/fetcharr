package me.egg82.fetcharr.api.model.update;

import me.egg82.arr.common.ArrAPI;
import me.egg82.fetcharr.api.FetcharrAPI;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

public abstract class AbstractUpdater implements Updater {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final FetcharrAPI api;
    protected final ArrAPI arrApi;
    protected final UpdaterConfig config;

    public AbstractUpdater(@NotNull FetcharrAPI api, @NotNull ArrAPI arrApi, @NotNull UpdaterConfig config) {
        this.api = api;
        this.arrApi = arrApi;
        this.config = config;
    }

    @Override
    public @NotNull ArrAPI arrApi() {
        return arrApi;
    }

    @Override
    public @NonNull UpdaterConfig config() {
        return config;
    }

    @Override
    public void run(@NotNull Instant previousRun) {
        try {
            doWork(previousRun);
        } catch (Exception ex) {
            logger.error("Exception in {} doWork method", getClass().getSimpleName(), ex);
        }
    }

    abstract protected void doWork(@NotNull Instant previousRun);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractUpdater that)) return false;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config);
    }

    @Override
    public String toString() {
        return "AbstractUpdater{" +
                "api=" + api +
                ", arrApi=" + arrApi +
                ", config=" + config +
                '}';
    }
}
