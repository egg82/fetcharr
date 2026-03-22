package me.egg82.fwebhook;

import me.egg82.fetcharr.api.plugin.Plugin;
import me.egg82.fetcharr.api.plugin.PluginContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Webhook implements Plugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Webhook() {
        logger.info("Loaded webhooks!");
    }

    @Override
    public void init(@NotNull PluginContext context) throws Exception {
        logger.info("Initialized webhooks!");
    }

    @Override
    public void start() throws Exception {
        logger.info("Started webhooks!");
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopped webhooks!");
    }
}
