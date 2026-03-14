package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MediaCover {
    private final MediaCoverType coverType;
    private final String remoteUrl;
    private final String url;
    private final String extension;

    public MediaCover(@NotNull JSONObject obj) {
        this.coverType = MediaCoverType.parse(MediaCoverType.UNKNOWN, StringParser.parse(obj, "coverType"));
        this.remoteUrl = StringParser.parse(obj, "remoteUrl");
        this.url = StringParser.parse(obj, "url");
        this.extension = StringParser.parse(obj, "extension");
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

    public @Nullable String extension() {
        return extension;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediaCover that)) return false;
        return coverType == that.coverType && Objects.equals(remoteUrl, that.remoteUrl) && Objects.equals(url, that.url) && Objects.equals(extension, that.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverType, remoteUrl, url, extension);
    }

    @Override
    public String toString() {
        return "MediaCover{" +
                "coverType=" + coverType +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", url='" + url + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
