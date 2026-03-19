package me.egg82.arr.lidarr.v1.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DownloadProtocol {
    UNKNOWN("unknown"),
    USENET("usenet"),
    TORRENT("torrent");

    private final String apiName;
    DownloadProtocol(@NotNull String apiName) {
        this.apiName = apiName;
    }

    public @NotNull String apiName() {
        return apiName;
    }

    public static @NotNull DownloadProtocol get(@NotNull DownloadProtocol def, @Nullable JSONObject obj, @Nullable String key) {
        DownloadProtocol r = get(obj, key);
        return r != null ? r : def;
    }

    public static @Nullable DownloadProtocol get(@Nullable JSONObject obj, @Nullable String key) {
        if (obj == null || key == null || key.isEmpty()) {
            return null;
        }
        return parse(StringParser.get(obj, key));
    }

    public static @NotNull DownloadProtocol parse(@NotNull DownloadProtocol def, @Nullable String val) {
        DownloadProtocol r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable DownloadProtocol parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (DownloadProtocol e : values()) {
            if (e.apiName.equalsIgnoreCase(val)) {
                return e;
            }
        }
        return null;
    }
}
