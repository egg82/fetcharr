package me.egg82.fetcharr.api.model.update;

import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.file.JSONFile;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.UpdaterPostDeregistrationEvent;
import me.egg82.fetcharr.api.event.update.UpdaterPostRegistrationEvent;
import me.egg82.fetcharr.api.event.update.UpdaterPreDeregistrationEvent;
import me.egg82.fetcharr.api.event.update.UpdaterPreRegistrationEvent;
import me.egg82.fetcharr.config.CommonConfigVars;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UpdateManagerImpl implements UpdateManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FetcharrAPI api;
    private final ScheduledExecutorService pool;

    private final List<@NotNull Updater> updaters = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public UpdateManagerImpl(@NotNull FetcharrAPI api, @NotNull ScheduledExecutorService pool) {
        this.api = api;
        this.pool = pool;

        run();
    }

    @Override
    public @NotNull PVector<@NotNull Updater> updaters() {
        try {
            lock.readLock().lock();
            return TreePVector.from(updaters);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean register(@NotNull Updater updater) {
        logger.debug("Registering {}_{}: {}", updater.config().type().name(), updater.config().id(), updater.config().url());

        UpdaterPreRegistrationEvent preEvent = new UpdaterPreRegistrationEvent(updater, api);
        api.bus().post(preEvent);
        if (preEvent.cancelled()) {
            return false;
        }

        try {
            lock.writeLock().lock();
            updaters.add(updater);
        } finally {
            lock.writeLock().unlock();
        }

        api.bus().post(new UpdaterPostRegistrationEvent(updater, api));
        return true;
    }

    @Override
    public boolean unregister(@NotNull Updater updater) {
        logger.debug("Unregistering {}_{}: {}", updater.config().type().name(), updater.config().id(), updater.config().url());

        UpdaterPreDeregistrationEvent preEvent = new UpdaterPreDeregistrationEvent(updater, api);
        api.bus().post(preEvent);
        if (preEvent.cancelled()) {
            return false;
        }

        try {
            lock.writeLock().lock();
            updaters.remove(updater);
        } finally {
            lock.writeLock().unlock();
        }

        api.bus().post(new UpdaterPostDeregistrationEvent(updater, api));
        return true;
    }

    @Override
    public void shutdown(long waitMillis) {
        logger.debug("Shutting down..");

        pool.shutdown();
        try {
            if (!pool.awaitTermination(waitMillis, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean dryRun() {
        return CommonConfigVars.getBool(CommonConfigVars.DRY_RUN);
    }

    private void run() {
        pool.schedule(this::run, 5, TimeUnit.SECONDS);

        // TODO: run updaters
    }

    private @NotNull File getBasePath(@NotNull Updater updater) {
        File base = CacheConfigVars.getFile(CacheConfigVars.CACHE_DIR);
        File arr = new File(base, updater.config().type().name().toLowerCase() + "-" + updater.config().id());
        return new File(arr, getClass().getSimpleName());
    }

    private boolean isCacheWritable() {
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
