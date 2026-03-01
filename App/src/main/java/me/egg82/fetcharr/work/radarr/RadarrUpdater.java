package me.egg82.fetcharr.work.radarr;

import me.egg82.fetcharr.env.ParsedTime;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.web.radarr.Movie;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class RadarrUpdater implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(RadarrUpdater.class);

    private final RadarrAPI api;
    private final WeightedRandom<Movie> random;

    private final int searchAmount;
    private final ParsedTime searchInterval;
    private final long intervalSeconds;

    private Instant lastUpdate;

    public RadarrUpdater(@NotNull RadarrAPI api) {
        this.api = api;
        this.random = new WeightedRandom<>();

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

        LOGGER.info("Updating max {} items for for RADARR_{} ({})", searchAmount, api.id(), api.url());

        random.updateList(api.movies());

        for (int i = 0; i < searchAmount; i++) {
            Movie x = random.selectOne();
            if (x != null) {
                LOGGER.info("Updating movie {} ({})", x.id(), x.title());
            }
        }
    }
}
