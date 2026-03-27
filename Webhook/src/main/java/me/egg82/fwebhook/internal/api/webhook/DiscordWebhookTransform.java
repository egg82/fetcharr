package me.egg82.fwebhook.internal.api.webhook;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.event.update.AbstractCancellableUpdaterEvent;
import me.egg82.fetcharr.api.event.update.SelectionCancellationReason;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrSearchEvent;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrSkipArtistSelectionEvent;
import me.egg82.fetcharr.api.event.update.lidarr.LidarrUpdateArtistEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrSearchEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrSkipMovieSelectionEvent;
import me.egg82.fetcharr.api.event.update.radarr.RadarrUpdateMovieEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrSearchEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrSkipSeriesSelectionEvent;
import me.egg82.fetcharr.api.event.update.sonarr.SonarrUpdateSeriesEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrSearchEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrSkipMovieSelectionEvent;
import me.egg82.fetcharr.api.event.update.whisparr.WhisparrUpdateMovieEvent;
import me.egg82.fwebhook.api.webhook.WebhookPayload;
import me.egg82.fwebhook.api.webhook.WebhookPayloadMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class DiscordWebhookTransform extends AbstractWebhookTransform {
    public DiscordWebhookTransform(@NotNull CommentedConfigurationNode config) {
        super(config);
    }

    @Override
    public boolean accepts(@NotNull FetcharrEvent event) {
        return (event instanceof RadarrSearchEvent && config.node("events", "search").getBoolean(true))
                || (event instanceof SonarrSearchEvent && config.node("events", "search").getBoolean(true))
                || (event instanceof LidarrSearchEvent && config.node("events", "search").getBoolean(true))
                || (event instanceof WhisparrSearchEvent && config.node("events", "search").getBoolean(true))
                || (event instanceof RadarrUpdateMovieEvent && config.node("events", "update").getBoolean(false))
                || (event instanceof SonarrUpdateSeriesEvent && config.node("events", "update").getBoolean(false))
                || (event instanceof LidarrUpdateArtistEvent && config.node("events", "update").getBoolean(false))
                || (event instanceof WhisparrUpdateMovieEvent && config.node("events", "update").getBoolean(false))
                || (event instanceof RadarrSkipMovieSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof SonarrSkipSeriesSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof LidarrSkipArtistSelectionEvent && config.node("events", "skip").getBoolean(false))
                || (event instanceof WhisparrSkipMovieSelectionEvent && config.node("events", "skip").getBoolean(false));
    }

    @Override
    public @Nullable WebhookPayload transform(@NotNull FetcharrEvent event) throws Exception {
        if (event instanceof RadarrSearchEvent e) {
            return transformInternal(e);
        } else if (event instanceof SonarrSearchEvent e) {
            return transformInternal(e);
        } else if (event instanceof LidarrSearchEvent e) {
            return transformInternal(e);
        } else if (event instanceof WhisparrSearchEvent e) {
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

    private @Nullable WebhookPayload transformInternal(@NotNull RadarrSearchEvent event) {
        return transformSearch(event, event.resources().size(), event.resources().stream().map(MovieResource::title).collect(Collectors.joining("\n")));
    }

    private @Nullable WebhookPayload transformInternal(@NotNull SonarrSearchEvent event) {
        return transformSearch(event, event.resources().size(), event.resources().stream().map(SeriesResource::title).collect(Collectors.joining("\n")));
    }

    private @Nullable WebhookPayload transformInternal(@NotNull LidarrSearchEvent event) {
        return transformSearch(event, event.resources().size(), event.resources().stream().map(ArtistResource::artistName).collect(Collectors.joining("\n")));
    }

    private @Nullable WebhookPayload transformInternal(@NotNull WhisparrSearchEvent event) {
        return transformSearch(event, event.resources().size(), event.resources().stream().map(me.egg82.arr.whisparr.v3.schema.MovieResource::title).collect(Collectors.joining("\n")));
    }

    private @Nullable WebhookPayload transformSearch(@NotNull AbstractCancellableUpdaterEvent event, int size, @Nullable String itemList) {
        if (!config.node("events", "search").getBoolean(true)) {
            return null;
        }

        String url = config.node("url").getString();
        if (url == null || url.isBlank()) {
            logger.warn("Configured URL for {} is empty", getClass().getSimpleName());
            return null;
        }

        JSONObject app = new JSONObject();
        app.put("name", "App");
        app.put("value", event.updater().config().type().name());
        app.put("inline", true);

        JSONObject count = new JSONObject();
        count.put("name", "Count");
        count.put("value", size);
        count.put("inline", true);

        JSONObject items = new JSONObject();
        items.put("name", "Items");
        items.put("value", itemList);
        items.put("inline", false);

        JSONArray fields = new JSONArray();
        fields.put(app);
        fields.put(count);
        fields.put(items);

        JSONObject embed = new JSONObject();
        embed.put("title", "Search requested");
        embed.put("description", "Fetcharr asked an *arr to search for items.");
        embed.put("fields", fields);

        JSONArray embeds = new JSONArray();
        embeds.put(embed);

        JSONObject obj = new JSONObject();
        obj.put("username", "Fetcharr");
        obj.put("embeds", embeds);
        return new WebhookPayloadImpl(url, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
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
        if (!config.node("events", "update").getBoolean(false)) {
            return null;
        }

        String url = config.node("url").getString();
        if (url == null || url.isBlank()) {
            logger.warn("Configured URL for {} is empty", getClass().getSimpleName());
            return null;
        }

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

        JSONObject embed = new JSONObject();
        embed.put("title", "Queued for update");
        embed.put("description", "Fetcharr selected an item and queued it for update.");
        embed.put("fields", fields);

        JSONArray embeds = new JSONArray();
        embeds.put(embed);

        JSONObject obj = new JSONObject();
        obj.put("username", "Fetcharr");
        obj.put("embeds", embeds);
        return new WebhookPayloadImpl(url, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
    }

    private @Nullable WebhookPayload transformSkip(@NotNull AbstractCancellableUpdaterEvent event, @Nullable String itemName, @NotNull SelectionCancellationReason cancellationReason) {
        if (!config.node("events", "skip").getBoolean(false)) {
            return null;
        }

        String url = config.node("url").getString();
        if (url == null || url.isBlank()) {
            logger.warn("Configured URL for {} is empty", getClass().getSimpleName());
            return null;
        }

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

        JSONObject embed = new JSONObject();
        embed.put("title", "Skipped item");
        embed.put("description", "Fetcharr skipped an item during selection.");
        embed.put("fields", fields);

        JSONArray embeds = new JSONArray();
        embeds.put(embed);

        JSONObject obj = new JSONObject();
        obj.put("username", "Fetcharr");
        obj.put("embeds", embeds);
        return new WebhookPayloadImpl(url, "application/json", "application/json", null, obj.toString().getBytes(StandardCharsets.UTF_8), WebhookPayloadMethod.POST);
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
        return "DiscordWebhookTransform{" +
                "config=" + config +
                '}';
    }
}
