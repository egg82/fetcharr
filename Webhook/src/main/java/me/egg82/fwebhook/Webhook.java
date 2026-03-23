package me.egg82.fwebhook;

import com.sasorio.event.EventConfig;
import me.egg82.fetcharr.api.FetcharrAPIProvider;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrUpdateArtistEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrUpdateMovieEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrUpdateSeriesEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrUpdateMovieEvent;
import me.egg82.fetcharr.api.plugin.Plugin;
import me.egg82.fetcharr.api.plugin.PluginContext;
import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.internal.api.WebhookAPIImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;

public class Webhook implements Plugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebhookAPI api = new WebhookAPIImpl();

    public Webhook() {
        // WebhookAPI can be retrieved by other plugins by calling WebhookAPIProvider.instance()
        // Or by calling FetcharrAPIProvider.instance().registry().getFirst(WebhookAPI.class)
        FetcharrAPIProvider.instance().registry().register(this, WebhookAPI.class, api);

        logger.info("Loaded Webhook plugin");
    }

    @Override
    public void init(@NotNull PluginContext context) throws Exception {
        CommentedConfigurationNode config = loadConfig(new File(context.configDir(), "config.yaml"));

        FetcharrAPIProvider.instance().events().subscribe(RadarrUpdateMovieEvent.class, EventConfig.of(Integer.MAX_VALUE, false, true), this::updateMovie);
        FetcharrAPIProvider.instance().events().subscribe(SonarrUpdateSeriesEvent.class, EventConfig.of(Integer.MAX_VALUE, false, true), this::updateSeries);
        FetcharrAPIProvider.instance().events().subscribe(LidarrUpdateArtistEvent.class, EventConfig.of(Integer.MAX_VALUE, false, true), this::updateArtist);
        FetcharrAPIProvider.instance().events().subscribe(WhisparrUpdateMovieEvent.class, EventConfig.of(Integer.MAX_VALUE, false, true), this::updateScene);

        logger.info("Initialized webhook plugin");
    }

    @Override
    public void start() throws Exception {
        logger.info("Started Webhook plugin");
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopped Webhook plugin");
    }

    private @NotNull CommentedConfigurationNode loadConfig(@NotNull File configFile) {

    }

    private void updateMovie(@NotNull RadarrUpdateMovieEvent event) {
        logger.debug("Webhook plugin caught update movie event for {}_{}", event.updater().config().type().name(), event.updater().config().id());
    }

    private void updateSeries(@NotNull SonarrUpdateSeriesEvent event) {
        logger.debug("Webhook plugin caught update series event for {}_{}", event.updater().config().type().name(), event.updater().config().id());
    }

    private void updateArtist(@NotNull LidarrUpdateArtistEvent event) {
        logger.debug("Webhook plugin caught update artist event for {}_{}", event.updater().config().type().name(), event.updater().config().id());
    }

    private void updateScene(@NotNull WhisparrUpdateMovieEvent event) {
        logger.debug("Webhook plugin caught update scene event for {}_{}", event.updater().config().type().name(), event.updater().config().id());
    }
}
