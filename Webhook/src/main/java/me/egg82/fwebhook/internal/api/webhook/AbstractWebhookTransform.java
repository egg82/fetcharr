package me.egg82.fwebhook.internal.api.webhook;

import me.egg82.fwebhook.api.webhook.WebhookTransform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Objects;

public abstract class AbstractWebhookTransform implements WebhookTransform {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final CommentedConfigurationNode config;

    public AbstractWebhookTransform(@NotNull CommentedConfigurationNode config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractWebhookTransform that)) return false;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(config);
    }

    @Override
    public String toString() {
        return "AbstractWebhookTransform{" +
                "config=" + config +
                '}';
    }
}
