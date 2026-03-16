package me.egg82.arr.sonarr.v3.schema;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediaCover extends AbstractAPIObject {
    private final MediaCoverTypes coverType;
    private final String url;
    private final String remoteUrl;

    public MediaCover(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.coverType = MediaCoverTypes.get(MediaCoverTypes.UNKNOWN, obj, "coverType");
        this.url = StringParser.get(obj, "url");
        this.remoteUrl = StringParser.get(obj, "remoteUrl");
    }

    public @NotNull MediaCoverTypes coverType() {
        return coverType;
    }

    public @Nullable String url() {
        return url;
    }

    public @Nullable String remoteUrl() {
        return remoteUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediaCover that)) return false;
        return coverType == that.coverType && Objects.equals(url, that.url) && Objects.equals(remoteUrl, that.remoteUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverType, url, remoteUrl);
    }

    @Override
    public String toString() {
        return "MediaCover{" +
                "coverType=" + coverType +
                ", url='" + url + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
