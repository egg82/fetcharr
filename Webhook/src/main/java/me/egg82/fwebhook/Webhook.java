package me.egg82.fwebhook;

import com.sasorio.event.EventConfig;
import me.egg82.fetcharr.api.FetcharrAPIProvider;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.plugin.Plugin;
import me.egg82.fetcharr.api.plugin.PluginContext;
import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.api.WebhookAPIProvider;
import me.egg82.fwebhook.api.webhook.WebhookDestination;
import me.egg82.fwebhook.api.webhook.WebhookTransform;
import me.egg82.fwebhook.internal.api.APIRegistrationUtil;
import me.egg82.fwebhook.internal.api.WebhookAPIImpl;
import me.egg82.fwebhook.internal.api.webhook.DiscordWebhookDestination;
import me.egg82.fwebhook.internal.api.webhook.DiscordWebhookTransform;
import me.egg82.fwebhook.internal.api.webhook.NotifiarrWebhookDestination;
import me.egg82.fwebhook.internal.api.webhook.NotifiarrWebhookTransform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.util.Locale;
import java.util.Map;

public class Webhook implements Plugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean started = false;

    public Webhook() {
        logger.info("Webhook plugin ready to init");
    }

    @Override
    public void init(@NotNull PluginContext context) throws Exception {
        WebhookAPI api = new WebhookAPIImpl();

        // This ensures that other plugins can retrieve WebhookAPI by calling
        // WebhookAPIProvider.instance()
        APIRegistrationUtil.register(api);

        // This ensures that other plugins can retrieve WebhookAPI by calling
        // FetcharrAPIProvider.instance().registry().getFirst(WebhookAPI.class)
        FetcharrAPIProvider.instance().registry().register(this, WebhookAPI.class, api);

        CommentedConfigurationNode config = loadConfig(new File(context.configDir(), "config.yaml"));
        if (config == null) {
            return;
        }

        for (Map.Entry<Object, CommentedConfigurationNode> child : config.node("webhooks").childrenMap().entrySet()) {
            String id = (String) child.getKey();
            String type = child.getValue().node("type").getString("").trim().toLowerCase(Locale.ROOT);
            if (type.isEmpty()) {
                logger.warn("Webhook {} type is not set", id);
                continue;
            }

            WebhookTransform transform = null;
            if (type.equalsIgnoreCase("discord")) {
                transform = new DiscordWebhookTransform(child.getValue());
            }
            if (type.equalsIgnoreCase("notifiarr")) {
                transform = new NotifiarrWebhookTransform(child.getValue());
            }
            if (transform == null) {
                logger.warn("Could not find transform for type {}", type);
                continue;
            }

            WebhookDestination destination = null;
            if (type.equalsIgnoreCase("discord")) {
                destination = new DiscordWebhookDestination(api, id, config, transform);
            }
            if (type.equalsIgnoreCase("notifiarr")) {
                destination = new NotifiarrWebhookDestination(api, id, config, transform);
            }
            if (destination == null) {
                logger.warn("Could not find destination for type {}", type);
                continue;
            }

            if (api.register(destination)) {
                logger.debug("Registered {} for {}", destination.getClass().getSimpleName(), id);
            } else {
                logger.warn("Destination registration was unsuccessful for {}", id);
            }
        }

        FetcharrAPIProvider.instance().events().subscribe(FetcharrEvent.class, EventConfig.of(Integer.MAX_VALUE, false, false), this::fetcharrEvent);

        logger.info("Initialized Webhook plugin");
    }

    @Override
    public void start() throws Exception {
        this.started = true;

        logger.info("Started Webhook plugin");
    }

    @Override
    public void stop() throws Exception {
        this.started = false;

        logger.info("Stopped Webhook plugin");
    }

    private @Nullable CommentedConfigurationNode loadConfig(@NotNull File configFile) {
        if (!configFile.exists() || !configFile.isFile() || configFile.length() == 0L) {
            try (InputStream resource = getClass().getResourceAsStream("/config.yaml"); FileWriter out = new FileWriter(configFile); BufferedWriter writer = new BufferedWriter(out)) {
                if (resource == null) {
                    logger.error("Could not get resource {}", "/config.yaml");
                    return null;
                }

                File parent = configFile.getParentFile();
                if (parent.exists() && !parent.isDirectory()) {
                    if (!parent.delete()) {
                        logger.error("Could not delete file {}", parent.getAbsolutePath());
                        return null;
                    }
                }
                if (!parent.exists() && !parent.mkdirs()) {
                    logger.error("Could not create directory {}", parent.getAbsolutePath());
                    return null;
                }

                byte[] buffer = new byte[250];
                int len;
                while ((len = resource.read(buffer)) > 0) {
                    char[] chars = new char[len];
                    for (int i = 0; i < len; i++) {
                        chars[i] = (char) buffer[i];
                    }
                    writer.write(chars);
                }
            } catch (IOException ex) {
                logger.error("Could not open resource {} or write file {}", "/config.yaml", configFile.getAbsolutePath(), ex);
                return null;
            }
        }

        try (FileReader file = new FileReader(configFile); BufferedReader reader = new BufferedReader(file)) {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .source(() -> reader)
                    .build();
            return loader.load();
        } catch (IOException ex) {
            logger.error("Could not read config file at {}", configFile.getAbsolutePath(), ex);
            return null;
        }
    }

    private void fetcharrEvent(@NotNull FetcharrEvent event) {
        if (!started) {
            // Don't handle events unless the plugin is in a "running" state
            // ie. don't attempt to handle events before Fetcharr starts plugins or after it stops them
            return;
        }

        WebhookAPI api = FetcharrAPIProvider.instance().registry().getFirst(WebhookAPI.class); // Be nice and let other plugins take over the API if they want to try
        if (api == null) {
            // Something went very wrong, and we still need an API, so fall back to one we know we control
            logger.warn("Fetcharr registry did not return valid WebhookAPI while handling event {}", event.eventType().getName());
            api = WebhookAPIProvider.instance();
        }

        for (WebhookDestination d : api.destinations()) {
            try {
                if (!d.accepts(event)) {
                    continue;
                }

                if (d.handle(event)) {
                    logger.debug("Destination {} ({}) handled event type {}", d.id(), d.type(), event.eventType().getName());
                } else {
                    logger.debug("Destination {} ({}) did not handle event type {}", d.id(), d.type(), event.eventType().getName());
                }
            } catch (Exception ex) {
                logger.warn("Destination {} ({}) threw an exception during handling of event {}", d.id(), d.type(), event.eventType().getName(), ex);
            }
        }
    }
}
