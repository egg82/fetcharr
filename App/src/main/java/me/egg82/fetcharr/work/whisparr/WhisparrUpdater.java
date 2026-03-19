package me.egg82.fetcharr.work.whisparr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.config.Tristate;
import me.egg82.arr.unit.TimeValue;
import me.egg82.arr.whisparr.WhisparrV3API;
import me.egg82.arr.whisparr.v3.Movie;
import me.egg82.arr.whisparr.v3.Tag;
import me.egg82.arr.whisparr.v3.schema.MovieFileResource;
import me.egg82.arr.whisparr.v3.schema.MovieResource;
import me.egg82.arr.whisparr.v3.schema.TagResource;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.WhisparrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhisparrUpdater extends AbstractUpdater {
    private final WeightedRandom<WeightedMovie> random = new WeightedRandom<>();

    public WhisparrUpdater(@NotNull WhisparrV3API api) {
        super(api);

        if (!Instant.EPOCH.equals(this.metaFile.lastUpdate())) {
            logger.debug("Resuming WHISPARR_{} from last update at {}", api.id(), this.metaFile.lastUpdate());
        }
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = WhisparrConfigVars.getTimeValue(WhisparrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = WhisparrConfigVars.getInt(WhisparrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for WHISPARR_{}: {}", searchAmount, api.id(), api.baseUrl());
        if (searchAmount <= 0) {
            logger.info("Skipping updating items (search amount {}) for WHISPARR_{}: {}", searchAmount, api.id(), api.baseUrl());
            return;
        }

        Movie all = api.fetch(Movie.class);
        if (all == null) {
            logger.error("WHISPARR_{} returned bad result for {}", api.id(), Movie.UNKNOWN.apiPath());
            return;
        }

        List<WeightedMovie> wrapped = new ArrayList<>();
        for (MovieResource m : all.resources()) {
            wrapped.add(new WeightedMovie(m));
        }
        random.updateList(wrapped);

        boolean monitoredOnly = WhisparrConfigVars.getBool(WhisparrConfigVars.MONITORED_ONLY, api.id());
        boolean missingOnly = WhisparrConfigVars.getBool(WhisparrConfigVars.MISSING_ONLY, api.id());
        boolean useCutoff = WhisparrConfigVars.getBool(WhisparrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = WhisparrConfigVars.getArr(WhisparrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = ConfigVars.getBool(ConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            WeightedMovie m = random.selectOne();
            if (m == null) {
                continue;
            }
            if (monitoredOnly && !m.resource().monitored()) {
                logger.info("Skipping scene/movie {} (\"{}\") due to unmonitored status", m.resource().id(), m.resource().title());
                continue;
            }
            if (missingOnly && m.resource().hasFile()) {
                logger.info("Skipping scene/movie {} (\"{}\") because it is not missing a movie file", m.resource().id(), m.resource().title());
                continue;
            }
            MovieFileResource movieFile = m.resource().movieFile();
            if (useCutoff && movieFile != null && !movieFile.qualityCutoffNotMet()) {
                logger.info("Skipping scene/movie {} (\"{}\") because it meets the quality cutoff", m.resource().id(), m.resource().title());
                continue;
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, m.resource().tags())) {
                logger.info("Skipping scene/movie {} (\"{}\") because skip-tag is set", m.resource().id(), m.resource().title());
                continue;
            }

            if (dryRun) {
                logger.info("Would update scene/movie {} (\"{}\") if not in dry-run mode", m.resource().id(), m.resource().title());
            } else {
                logger.info("Updating scene/movie {} (\"{}\")", m.resource().id(), m.resource().title());
            }
            ids.add(m.resource().id());
            api.invalidate(Movie.class, m.resource().id()); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.metaFile.lastUpdate(lastUpdate);
        Tristate fileCache = CacheConfigVars.getTristate(CacheConfigVars.USE_FILE_CACHE);
        if ((fileCache == Tristate.AUTO && isCacheWritable()) || fileCache == Tristate.TRUE) {
            this.metaFile.write();
        }
    }

    private boolean hasAnyTag(@NotNull String @NotNull [] needles, @NotNull Collection<@NotNull Tag> haystack) {
        if (needles.length == 0 || haystack.isEmpty()) {
            return false;
        }

        for (String n : needles) {
            for (Tag t : haystack) {
                TagResource r = t.resource();
                if (r != null) {
                    String label = r.label();
                    if (label != null && label.equalsIgnoreCase(n)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
