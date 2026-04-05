package me.egg82.fuiplugin;

import me.egg82.arr.lidarr.v1.schema.ArtistResource;
import me.egg82.arr.radarr.v3.schema.MovieResource;
import me.egg82.arr.sonarr.v3.schema.SeriesResource;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.event.update.lidarr.*;
import me.egg82.fetcharr.api.event.update.radarr.*;
import me.egg82.fetcharr.api.event.update.sonarr.*;
import me.egg82.fetcharr.api.event.update.whisparr.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public record EventEntry(
        @NotNull Instant time,
        @NotNull String type,
        @NotNull String service,
        @NotNull String updaterUrl,
        @NotNull String title,
        boolean cancelled
) {
    public static @NotNull EventEntry from(@NotNull FetcharrEvent event) {
        Instant time = Instant.now();
        String updaterUrl = "";

        if (event instanceof AbstractUpdaterEvent ae) {
            try {
                updaterUrl = ae.updater().config().url();
            } catch (Exception ignored) { }
        }

        // Radarr
        if (event instanceof RadarrUpdateMovieEvent e) {
            return new EventEntry(time, "RadarrUpdateMovie", "radarr", updaterUrl, radarrTitle(e.resource()), false);
        }
        if (event instanceof RadarrSelectMovieEvent e) {
            return new EventEntry(time, "RadarrSelectMovie", "radarr", updaterUrl, radarrTitle(e.resource()), false);
        }
        if (event instanceof RadarrSkipMovieSelectionEvent e) {
            return new EventEntry(time, "RadarrSkipMovie", "radarr", updaterUrl,
                    radarrTitle(e.resource()) + " [" + e.reason().name() + "]", true);
        }
        if (event instanceof RadarrFetchMovieEvent) {
            return new EventEntry(time, "RadarrFetchMovie", "radarr", updaterUrl, "fetched from API", false);
        }
        if (event instanceof RadarrSearchEvent e) {
            int count = e.resources().size();
            return new EventEntry(time, "RadarrSearch", "radarr", updaterUrl, count + " movie" + (count != 1 ? "s" : ""), false);
        }

        // Sonarr
        if (event instanceof SonarrUpdateSeriesEvent e) {
            return new EventEntry(time, "SonarrUpdateSeries", "sonarr", updaterUrl, sonarrTitle(e.resource()), false);
        }
        if (event instanceof SonarrSelectSeriesEvent e) {
            return new EventEntry(time, "SonarrSelectSeries", "sonarr", updaterUrl, sonarrTitle(e.resource()), false);
        }
        if (event instanceof SonarrSkipSeriesSelectionEvent e) {
            return new EventEntry(time, "SonarrSkipSeries", "sonarr", updaterUrl,
                    sonarrTitle(e.resource()) + " [" + e.reason().name() + "]", true);
        }
        if (event instanceof SonarrFetchEpisodesEvent e) {
            return new EventEntry(time, "SonarrFetchEpisodes", "sonarr", updaterUrl, sonarrTitle(e.series()), false);
        }
        if (event instanceof SonarrFetchSeriesEvent) {
            return new EventEntry(time, "SonarrFetchSeries", "sonarr", updaterUrl, "fetched from API", false);
        }
        if (event instanceof SonarrSearchEvent e) {
            int count = e.resources().size();
            return new EventEntry(time, "SonarrSearch", "sonarr", updaterUrl, count + " series", false);
        }

        // Lidarr
        if (event instanceof LidarrUpdateArtistEvent e) {
            return new EventEntry(time, "LidarrUpdateArtist", "lidarr", updaterUrl, lidarrTitle(e.resource()), false);
        }
        if (event instanceof LidarrSelectArtistEvent e) {
            return new EventEntry(time, "LidarrSelectArtist", "lidarr", updaterUrl, lidarrTitle(e.resource()), false);
        }
        if (event instanceof LidarrSkipArtistSelectionEvent e) {
            return new EventEntry(time, "LidarrSkipArtist", "lidarr", updaterUrl,
                    lidarrTitle(e.resource()) + " [" + e.reason().name() + "]", true);
        }
        if (event instanceof LidarrFetchAlbumsEvent e) {
            return new EventEntry(time, "LidarrFetchAlbums", "lidarr", updaterUrl, lidarrTitle(e.artist()), false);
        }
        if (event instanceof LidarrFetchTracksEvent e) {
            return new EventEntry(time, "LidarrFetchTracks", "lidarr", updaterUrl, lidarrTitle(e.artist()), false);
        }
        if (event instanceof LidarrFetchArtistEvent) {
            return new EventEntry(time, "LidarrFetchArtist", "lidarr", updaterUrl, "fetched from API", false);
        }
        if (event instanceof LidarrSearchEvent e) {
            int count = e.resources().size();
            return new EventEntry(time, "LidarrSearch", "lidarr", updaterUrl, count + " artist" + (count != 1 ? "s" : ""), false);
        }

        // Whisparr
        if (event instanceof WhisparrUpdateMovieEvent e) {
            return new EventEntry(time, "WhisparrUpdateMovie", "whisparr", updaterUrl, whisparrTitle(e.resource()), false);
        }
        if (event instanceof WhisparrSelectMovieEvent e) {
            return new EventEntry(time, "WhisparrSelectMovie", "whisparr", updaterUrl, whisparrTitle(e.resource()), false);
        }
        if (event instanceof WhisparrSkipMovieSelectionEvent e) {
            return new EventEntry(time, "WhisparrSkipMovie", "whisparr", updaterUrl,
                    whisparrTitle(e.resource()) + " [" + e.reason().name() + "]", true);
        }
        if (event instanceof WhisparrFetchMovieEvent) {
            return new EventEntry(time, "WhisparrFetchMovie", "whisparr", updaterUrl, "fetched from API", false);
        }
        if (event instanceof WhisparrSearchEvent e) {
            int count = e.resources().size();
            return new EventEntry(time, "WhisparrSearch", "whisparr", updaterUrl, count + " movie" + (count != 1 ? "s" : ""), false);
        }

        return new EventEntry(time, event.eventType().getSimpleName(), "unknown", updaterUrl, "", false);
    }

    private static @NotNull String radarrTitle(@Nullable MovieResource r) {
        if (r == null) return "Unknown";
        String title = r.getTitle();
        if (title == null || title.isBlank()) return "Unknown";
        Integer year = r.getYear();
        return (year != null && year > 0) ? title + " (" + year + ")" : title;
    }

    private static @NotNull String sonarrTitle(@Nullable SeriesResource r) {
        if (r == null) return "Unknown";
        String title = r.getTitle();
        return (title != null && !title.isBlank()) ? title : "Unknown";
    }

    private static @NotNull String lidarrTitle(@Nullable ArtistResource r) {
        if (r == null) return "Unknown";
        String name = r.getArtistName();
        return (name != null && !name.isBlank()) ? name : "Unknown";
    }

    private static @NotNull String whisparrTitle(@Nullable me.egg82.arr.whisparr.v3.schema.MovieResource r) {
        if (r == null) return "Unknown";
        String title = r.getTitle();
        if (title == null || title.isBlank()) return "Unknown";
        Integer year = r.getYear();
        return (year != null && year > 0) ? title + " (" + year + ")" : title;
    }

    /** Serialize to a JSON object string (no external library needed). */
    public @NotNull String toJson() {
        return "{" +
                "\"time\":\"" + jsonEscape(time.toString()) + "\"," +
                "\"type\":\"" + jsonEscape(type) + "\"," +
                "\"service\":\"" + jsonEscape(service) + "\"," +
                "\"updaterUrl\":\"" + jsonEscape(updaterUrl) + "\"," +
                "\"title\":\"" + jsonEscape(title) + "\"," +
                "\"cancelled\":" + cancelled +
                "}";
    }

    private static @NotNull String jsonEscape(@NotNull String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
