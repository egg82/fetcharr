package me.egg82.fetcharr.work.radarr;

import me.egg82.fetcharr.env.ParsedTime;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.web.common.Tag;
import me.egg82.fetcharr.web.radarr.Movie;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RadarrUpdater implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(RadarrUpdater.class);

    private final RadarrAPI api;
    private final WeightedRandom<Movie> random;

    private final boolean monitoredOnly;
    private final String[] skipTags;
    private final int searchAmount;
    private final ParsedTime searchInterval;
    private final long intervalSeconds;

    private Instant lastUpdate;

    public RadarrUpdater(@NotNull RadarrAPI api) {
        this.api = api;
        this.random = new WeightedRandom<>();

        this.monitoredOnly = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_MONITORED_ONLY, api.id(), (Boolean) RadarrConfigVars.RADARR_MONITORED_ONLY.def());
        String st = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_SKIP_TAGS, api.id());
        this.skipTags = st != null ? st.split(",") : null;
        this.searchAmount = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_SEARCH_AMOUNT, api.id(), (Integer) RadarrConfigVars.RADARR_SEARCH_AMOUNT.def());
        this.searchInterval = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_SEARCH_INTERVAL, api.id(), (ParsedTime) RadarrConfigVars.RADARR_SEARCH_INTERVAL.def());
        this.intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());

        this.lastUpdate = Instant.EPOCH;
    }

    @Override
    public void run() {
        Instant now = Instant.now();
        if (Duration.between(lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }

        lastUpdate = now;

        LOGGER.info("Updating up to {} items for for RADARR_{}: {}", searchAmount, api.id(), api.url());

        random.updateList(api.movies());
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < searchAmount; i++) {
            Movie x = random.selectOne();
            while (
                    (monitoredOnly && x != null && !x.monitored())
                    || (skipTags != null && x != null && hasAnyTag(skipTags, x.tags()))
            ) {
                x = random.selectOne();
            }

            if (x == null) {
                continue;
            }

            LOGGER.info("Updating movie {}: {}", x.id(), x.title());
            ids.add(x.id());
        }

        api.search(ids);

        try {
            Thread.sleep(60_000L); // Let the *arr do its thing
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        for (int x : ids) {
            api.movie(x, false); // Force refresh cache
        }
    }

    private boolean hasAnyTag(@NotNull String @NotNull [] needles, @NotNull Set<@NotNull Tag> haystack) {
        for (String n : needles) {
            for (Tag t : haystack) {
                if (t.label().equalsIgnoreCase(n)) {
                    return true;
                }
            }
        }
        return false;
    }
}
