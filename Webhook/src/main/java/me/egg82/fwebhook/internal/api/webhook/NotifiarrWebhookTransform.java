package me.egg82.fwebhook.internal.api.webhook;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.event.update.APISearchEvent;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrSkipArtistSelectionEvent;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrUpdateArtistEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrSkipMovieSelectionEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrUpdateMovieEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrSkipSeriesSelectionEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrUpdateSeriesEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrSkipMovieSelectionEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrUpdateMovieEvent;
import me.egg82.fwebhook.api.webhook.WebhookPayload;
import me.egg82.fwebhook.api.webhook.WebhookPayloadMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class NotifiarrWebhookTransform extends AbstractWebhookTransform {
    public NotifiarrWebhookTransform(@NotNull CommentedConfigurationNode config) {
        super(config);
    }

    @Override
    public boolean accepts(@NotNull FetcharrEvent event) {
        return (event instanceof APISearchEvent && config.node("events", "search").getBoolean(false))
                || (event instanceof RadarrUpdateMovieEvent && config.node("events", "update").getBoolean(true))
                || (event instanceof SonarrUpdateSeriesEvent && config.node("events", "update").getBoolean(true))
                || (event instanceof LidarrUpdateArtistEvent && config.node("events", "update").getBoolean(true))
                || (event instanceof WhisparrUpdateMovieEvent && config.node("events", "update").getBoolean(true))
                || (event instanceof RadarrSkipMovieSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof SonarrSkipSeriesSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof LidarrSkipArtistSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof WhisparrSkipMovieSelectionEvent && config.node("events", "skip").getBoolean(false));
    }

    @Override
    public @Nullable WebhookPayload transform(@NotNull FetcharrEvent event) throws Exception {
        if (event instanceof APISearchEvent e) {
            return transformInternal(e);
        } else if (event instanceof RadarrUpdateMovieEvent e) {
            return transformInternal(e);
        } else if (event instanceof SonarrUpdateSeriesEvent e) {
            return transformInternal(e);
        } else if (event instanceof LidarrUpdateArtistEvent e) {
            return transformInternal(e);
        } else if (event instanceof WhisparrUpdateMovieEvent e) {
            return transformInternal(e);
        } else if (event instanceof RadarrSkipMovieSelectionEvent e) {
            return transformInternal(e);
        } else if (event instanceof SonarrSkipSeriesSelectionEvent e) {
            return transformInternal(e);
        } else if (event instanceof LidarrSkipArtistSelectionEvent e) {
            return transformInternal(e);
        } else if (event instanceof WhisparrSkipMovieSelectionEvent e) {
            return transformInternal(e);
        }
        return null;
    }

    // ChatGPT largely came up with the structure and language of these embeds

    private @Nullable WebhookPayload transformInternal(@NotNull APISearchEvent event) {
        if (!config.node("events", "search").getBoolean(false)) {
            return null;
        }

        String apiKey = config.node("api-key").getString();
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Configured API key for {} is empty", getClass().getSimpleName());
            return null;
        }

        JSONObject notification = new JSONObject();
        notification.put("name", "Fetcharr");

        JSONObject ping = new JSONObject();
        ping.put("pingUser", config.node("ping", "user-id").getLong(0L));
        ping.put("pingRole", config.node("ping", "role-id").getLong(0L));

        JSONObject app = new JSONObject();
        app.put("name", "App");
        app.put("value", event.updater().config().type().name());
        app.put("inline", true);

        JSONObject count = new JSONObject();
        count.put("name", "Count");
        count.put("value", event.ids().size());
        count.put("inline", true);

        JSONObject ids = new JSONObject();
        ids.put("name", "IDs");
        // Source - https://stackoverflow.com/a/57784525
        // Posted by Hemmels
        // Retrieved 2026-03-27, License - CC BY-SA 4.0
        ids.put("value", event.ids().intStream().mapToObj(String::valueOf).collect(Collectors.joining(", ")));
        ids.put("inline", false);

        JSONArray fields = new JSONArray();
        fields.put(app);
        fields.put(count);
        fields.put(ids);

        JSONObject text = new JSONObject();
        text.put("title", "Search requested");
        text.put("description", "Fetcharr asked an *arr to search for items.");
        text.put("fields", fields);

        JSONObject channels = new JSONObject();
        channels.put("channel", config.node("channel-id").getLong(123456789012345678L));

        JSONObject discord = new JSONObject();
        discord.put("ping", ping);
        discord.put("text", text);
        discord.put("ids", channels);

        JSONObject obj = new JSONObject();
        obj.put("notification", notification);
        obj.put("discord", discord);
        return new WebhookPayloadImpl(apiKey, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
    }

    private @Nullable WebhookPayload transformInternal(@NotNull RadarrUpdateMovieEvent event) {
        return transformUpdate(event, event.resource().title());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull SonarrUpdateSeriesEvent event) {
        return transformUpdate(event, event.resource().title());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull LidarrUpdateArtistEvent event) {
        return transformUpdate(event, event.resource().artistName());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull WhisparrUpdateMovieEvent event) {
        return transformUpdate(event, event.resource().title());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull RadarrSkipMovieSelectionEvent event) {
        return transformSkip(event, event.resource().title(), event.reason());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull SonarrSkipSeriesSelectionEvent event) {
        return transformSkip(event, event.resource().title(), event.reason());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull LidarrSkipArtistSelectionEvent event) {
        return transformSkip(event, event.resource().artistName(), event.reason());
    }

    private @Nullable WebhookPayload transformInternal(@NotNull WhisparrSkipMovieSelectionEvent event) {
        return transformSkip(event, event.resource().title(), event.reason());
    }

    private @Nullable WebhookPayload transformUpdate(@NotNull AbstractCancellableUpdaterEvent event, @Nullable String itemName) {
        if (!config.node("events", "update").getBoolean(true)) {
            return null;
        }

        String apiKey = config.node("api-key").getString();
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Configured API key for {} is empty", getClass().getSimpleName());
            return null;
        }

        JSONObject notification = new JSONObject();
        notification.put("name", "Fetcharr");

        JSONObject ping = new JSONObject();
        ping.put("pingUser", config.node("ping", "user-id").getLong(0L));
        ping.put("pingRole", config.node("ping", "role-id").getLong(0L));

        JSONObject app = new JSONObject();
        app.put("name", "App");
        app.put("value", event.updater().config().type().name());
        app.put("inline", true);

        JSONObject item = new JSONObject();
        item.put("name", "Item");
        item.put("value", itemName != null && !itemName.isBlank() ? itemName : "<unknown>");
        item.put("inline", true);

        JSONArray fields = new JSONArray();
        fields.put(app);
        fields.put(item);

        JSONObject text = new JSONObject();
        text.put("title", "Queued for update");
        text.put("description", "Fetcharr selected an item and queued it for update.");
        text.put("fields", fields);

        JSONObject channels = new JSONObject();
        channels.put("channel", config.node("channel-id").getLong(123456789012345678L));

        JSONObject discord = new JSONObject();
        discord.put("ping", ping);
        discord.put("text", text);
        discord.put("ids", channels);

        JSONObject obj = new JSONObject();
        obj.put("notification", notification);
        obj.put("discord", discord);
        return new WebhookPayloadImpl(apiKey, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
    }

    private @Nullable WebhookPayload transformSkip(@NotNull AbstractCancellableUpdaterEvent event, @Nullable String itemName, @NotNull SelectionCancellationReason cancellationReason) {
        if (!config.node("events", "skip").getBoolean(true)) {
            return null;
        }

        String apiKey = config.node("api-key").getString();
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Configured API key for {} is empty", getClass().getSimpleName());
            return null;
        }

        JSONObject notification = new JSONObject();
        notification.put("name", "Fetcharr");

        JSONObject ping = new JSONObject();
        ping.put("pingUser", config.node("ping", "user-id").getLong(0L));
        ping.put("pingRole", config.node("ping", "role-id").getLong(0L));

        JSONObject app = new JSONObject();
        app.put("name", "App");
        app.put("value", event.updater().config().type().name());
        app.put("inline", true);

        JSONObject item = new JSONObject();
        item.put("name", "Item");
        item.put("value", itemName != null && !itemName.isBlank() ? itemName : "<unknown>");
        item.put("inline", true);

        JSONObject reason = new JSONObject();
        reason.put("name", "Reason");
        reason.put("value", mapReason(cancellationReason));
        reason.put("inline", true);

        JSONArray fields = new JSONArray();
        fields.put(app);
        fields.put(item);
        fields.put(reason);

        JSONObject text = new JSONObject();
        text.put("title", "Skipped item");
        text.put("description", "Fetcharr skipped an item during selection.");
        text.put("fields", fields);

        JSONObject channels = new JSONObject();
        channels.put("channel", config.node("channel-id").getLong(123456789012345678L));

        JSONObject discord = new JSONObject();
        discord.put("ping", ping);
        discord.put("text", text);
        discord.put("ids", channels);

        JSONObject obj = new JSONObject();
        obj.put("notification", notification);
        obj.put("discord", discord);
        return new WebhookPayloadImpl(apiKey, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
    }

    private @NotNull String mapReason(@NotNull SelectionCancellationReason reason) {
        return switch (reason) {
            case UNKNOWN -> "Unknown";
            case PLUGIN -> "Skipped by plugin";
            case UNMONITORED -> "Unmonitored";
            case NOT_MISSING -> "Not missing";
            case QUALITY_CUTOFF_MET -> "Quality cutoff met";
            case SKIP_TAG_FOUND -> "Skip tag found";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return "NotifiarrWebhookTransform{" +
                "config=" + config +
                '}';
    }
}
