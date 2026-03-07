package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediaCover {
    private final MediaCoverType coverType;
    private final String remoteUrl;
    private final String url;

    public MediaCover(@NotNull JSONObject obj) {
        this.coverType = MediaCoverType.parse(MediaCoverType.UNKNOWN, obj.getString("coverType"));
        this.remoteUrl = obj.getString("remoteUrl");
        this.url = obj.getString("url");
    }

    public @NotNull MediaCoverType coverType() {
        return coverType;
    }

    public @Nullable String remoteUrl() {
        return remoteUrl;
    }

    public @Nullable String url() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediaCover that)) return false;
        return coverType == that.coverType && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverType, url);
    }

    @Override
    public String toString() {
        return "MediaCover{" +
                "coverType=" + coverType +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
