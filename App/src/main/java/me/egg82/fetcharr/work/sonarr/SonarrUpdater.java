package me.egg82.fetcharr.work.sonarr;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.SonarrConfigVars;
import me.egg82.fetcharr.unit.TimeValue;
import me.egg82.fetcharr.util.WeightedRandom;
import me.egg82.fetcharr.web.model.sonarr.AllEpisodes;
import me.egg82.fetcharr.web.model.sonarr.AllSeries;
import me.egg82.fetcharr.web.model.sonarr.Episode;
import me.egg82.fetcharr.web.model.sonarr.Series;
import me.egg82.fetcharr.web.sonarr.SonarrAPI;
import me.egg82.fetcharr.work.AbstractUpdater;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class SonarrUpdater extends AbstractUpdater {
    private final WeightedRandom<Series> random = new WeightedRandom<>();

    public SonarrUpdater(@NotNull SonarrAPI api) {
        super(api);
    }

    @Override
    protected void doWork() {
        TimeValue searchInterval = SonarrConfigVars.getTimeValue(SonarrConfigVars.SEARCH_INTERVAL, api.id());
        long intervalSeconds = searchInterval.unit().toSeconds(searchInterval.time());
        Instant now = Instant.now();
        if (Duration.between(this.lastUpdate, now).getSeconds() < intervalSeconds) {
            return;
        }
        this.lastUpdate = now;

        int searchAmount = SonarrConfigVars.getInt(SonarrConfigVars.SEARCH_AMOUNT, api.id());

        logger.info("Updating up to {} items for for SONARR_{}: {}", searchAmount, api.id(), api.baseUrl());

        AllSeries all = api.fetch(AllSeries.class, false);
        random.updateList(all.items());

        boolean monitoredOnly = SonarrConfigVars.getBool(SonarrConfigVars.MONITORED_ONLY, api.id());
        boolean useCutoff = SonarrConfigVars.getBool(SonarrConfigVars.USE_CUTOFF, api.id());
        String[] skipTags = SonarrConfigVars.getArr(SonarrConfigVars.SKIP_TAGS, api.id());

        boolean dryRun = ConfigVars.getBool(ConfigVars.DRY_RUN);

        IntList ids = new IntArrayList();
        int attempts = 100;
        while (attempts > 0 && ids.size() < searchAmount) {
            attempts--;

            Series s = random.selectOne();
            if (s == null) {
                continue;
            }
            api.update(s);
            if (monitoredOnly && !s.monitored()) {
                logger.info("Skipping series {} (\"{}\") due to unmonitored status", s.id(), s.title());
                continue;
            }
            if (useCutoff) {
                boolean qualityCutoffMet = true;
                AllEpisodes a = api.fetch(AllEpisodes.class, false);
                for (Episode e : a.items()) {
                    if (!e.episodeFile().qualityCutoffNotMet()) {
                        qualityCutoffMet = false;
                        break;
                    }
                }
                if (qualityCutoffMet) {
                    logger.info("Skipping series {} (\"{}\") because it meets the quality cutoff", s.id(), s.title());
                    continue;
                }
            }
            if (skipTags.length > 0 && hasAnyTag(skipTags, s.tags())) {
                logger.info("Skipping series {} (\"{}\") because skip-tag is set", s.id(), s.title());
                continue;
            }

            if (dryRun) {
                logger.info("Would update series {} (\"{}\") if not in dry-run mode", s.id(), s.title());
            } else {
                logger.info("Updating series {} (\"{}\")", s.id(), s.title());
            }
            ids.add(s.id());
            s.invalidate(); // Force refresh on next
        }

        if (!dryRun && !ids.isEmpty()) {
            api.search(ids);
        }

        this.meta.last(lastUpdate);
        this.meta.write();
    }
}
