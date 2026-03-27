package me.egg82.fetcharr.api.model.update;

import me.egg82.arr.common.ArrType;
import me.egg82.arr.unit.TimeValue;
import me.egg82.fetcharr.config.ArrConfigVars;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PSet;
import org.pcollections.TreePSet;

public class UpdaterConfigImpl implements UpdaterConfig {
    private final ArrType type;
    private final int id;

    public UpdaterConfigImpl(@NotNull ArrType type, int id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public @NotNull ArrType type() {
        return this.type;
    }

    @Override
    public @NotNull String url() {
        String r = ArrConfigVars.get(ArrConfigVars.URL, type, id);
        if (r == null) {
            throw new IllegalStateException("URL for " + type.name() + "_" + id + " is null");
        }
        return r;
    }

    @Override
    public @NotNull String key() {
        String r = ArrConfigVars.get(ArrConfigVars.API_KEY, type, id);
        if (r == null) {
            throw new IllegalStateException("API_KEY for " + type.name() + "_" + id + " is null");
        }
        return r;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public int searchAmount() {
        return ArrConfigVars.getInt(ArrConfigVars.SEARCH_AMOUNT, type, id);
    }

    @Override
    public @NotNull TimeValue searchInterval() {
        return ArrConfigVars.getTimeValue(ArrConfigVars.SEARCH_INTERVAL, type, id);
    }

    @Override
    public boolean monitoredOnly() {
        return ArrConfigVars.getBool(ArrConfigVars.MONITORED_ONLY, type, id);
    }

    @Override
    public boolean missingOnly() {
        return ArrConfigVars.getBool(ArrConfigVars.MISSING_ONLY, type, id);
    }

    @Override
    public @NotNull PSet<@NotNull String> skipTags() {
        return TreePSet.of(ArrConfigVars.getArr(ArrConfigVars.SKIP_TAGS, type, id));
    }

    @Override
    public boolean useCutoff() {
        return ArrConfigVars.getBool(ArrConfigVars.USE_CUTOFF, type, id);
    }
}
