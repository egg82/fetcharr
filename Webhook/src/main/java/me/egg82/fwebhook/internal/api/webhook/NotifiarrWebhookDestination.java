package me.egg82.fwebhook.internal.api.webhook;

import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.api.webhook.WebhookTransform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class NotifiarrWebhookDestination extends AbstractWebhookDestination {
    public NotifiarrWebhookDestination(@NotNull WebhookAPI api, @NotNull String id, @NotNull CommentedConfigurationNode config, @NotNull WebhookTransform transform) {
        super(api, id, config, transform);
    }

    @Override
    public @NotNull String type() {
        return "notifiarr";
    }

    @Override
    public boolean accepts(@NotNull FetcharrEvent event) {
        if (!config.node("enabled").getBoolean(false)) {
            return false;
        }

        return super.accepts(event);
    }

    @Override
    public boolean handle(@NotNull FetcharrEvent event) throws Exception {
        if (!config.node("enabled").getBoolean(false)) {
            logger.debug("{} disabled - not handling event {}", getClass().getSimpleName(), event.eventType().getName());
            return false;
        }

        String response = handleInternal(event, config.node("url").getString("https://notifiarr.com/api/v1/notification/passthrough/"));
        if (response == null) {
            return false;
        }
        if (!response.isBlank()) {
            logger.debug("{} {} returned response for event {}: {}", getClass().getSimpleName(), this.id, event.eventType().getName(), response);
        }
        return true;
    }

    @Override
    public String toString() {
        return "NotifiarrWebhookDestination{" +
                "api=" + api +
                ", config=" + config +
                ", id='" + id + '\'' +
                ", transform=" + transform +
                '}';
    }
}
