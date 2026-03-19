package me.egg82.arr.lidarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.DurationParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.ObjectParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

public class ParsedTrackInfo extends AbstractAPIObject {
    private final String title;
    private final String cleanTitle;
    private final String artistTitle;
    private final String albumTitle;
    private final ArtistTitleInfo artistTitleInfo;
    private final String artistMBId;
    private final String albumMBId;
    private final String releaseMBId;
    private final String recordingMBId;
    private final String trackMBId;
    private final int discNumber;
    private final int discCount;
    private final IsoCountry country;
    private final int year;
    private final String label;
    private final String catalogNumber;
    private final String disambiguation;
    private final Duration duration;
    private final QualityModel quality;
    private final MediaInfoModel mediaInfo;
    private final IntList trackNumbers = new IntArrayList();
    private final String releaseGroup;
    private final String releaseHash;

    public ParsedTrackInfo(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.title = StringParser.get(obj, "title");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");
        this.artistTitle = StringParser.get(obj, "artistTitle");
        this.albumTitle = StringParser.get(obj, "albumTitle");
        this.artistTitleInfo = ObjectParser.get(ArtistTitleInfo.class, api, obj, "artistTitleInfo");
        this.artistMBId = StringParser.get(obj, "artistMBId");
        this.albumMBId = StringParser.get(obj, "albumMBId");
        this.releaseMBId = StringParser.get(obj, "releaseMBId");
        this.recordingMBId = StringParser.get(obj, "recordingMBId");
        this.trackMBId = StringParser.get(obj, "trackMBId");
        this.discNumber = NumberParser.getInt(-1, obj, "discNumber");
        this.discCount = NumberParser.getInt(-1, obj, "discCount");
        this.country = ObjectParser.get(IsoCountry.class, api, obj, "country");
        this.year = NumberParser.getInt(-1, obj, "year");
        this.label = StringParser.get(obj, "label");
        this.catalogNumber = StringParser.get(obj, "catalogNumber");
        this.disambiguation = StringParser.get(obj, "disambiguation");
        this.duration = DurationParser.get(obj, "duration");
        this.quality = ObjectParser.get(QualityModel.class, api, obj, "quality");
        this.mediaInfo = ObjectParser.get(MediaInfoModel.class, api, obj, "mediaInfo");

        JSONArray trackNumbers = obj.has("trackNumbers") && obj.get("trackNumbers") != null ? obj.getJSONArray("trackNumbers") : null;
        if (trackNumbers != null) {
            for (int i = 0; i < trackNumbers.length(); i++) {
                this.trackNumbers.add(trackNumbers.getInt(i));
            }
        }

        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.releaseHash = StringParser.get(obj, "releaseHash");
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public @Nullable String artistTitle() {
        return artistTitle;
    }

    public @Nullable String albumTitle() {
        return albumTitle;
    }

    public @Nullable ArtistTitleInfo artistTitleInfo() {
        return artistTitleInfo;
    }

    public @Nullable String artistMBId() {
        return artistMBId;
    }

    public @Nullable String albumMBId() {
        return albumMBId;
    }

    public @Nullable String releaseMBId() {
        return releaseMBId;
    }

    public @Nullable String recordingMBId() {
        return recordingMBId;
    }

    public @Nullable String trackMBId() {
        return trackMBId;
    }

    public int discNumber() {
        return discNumber;
    }

    public int discCount() {
        return discCount;
    }

    public @Nullable IsoCountry country() {
        return country;
    }

    public int year() {
        return year;
    }

    public @Nullable String label() {
        return label;
    }

    public @Nullable String catalogNumber() {
        return catalogNumber;
    }

    public @Nullable String disambiguation() {
        return disambiguation;
    }

    public @Nullable Duration duration() {
        return duration;
    }

    public @Nullable QualityModel quality() {
        return quality;
    }

    public @Nullable MediaInfoModel mediaInfo() {
        return mediaInfo;
    }

    public @NotNull IntList trackNumbers() {
        return trackNumbers;
    }

    public @Nullable String releaseGroup() {
        return releaseGroup;
    }

    public @Nullable String releaseHash() {
        return releaseHash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParsedTrackInfo that)) return false;
        return discNumber == that.discNumber && discCount == that.discCount && year == that.year && Objects.equals(title, that.title) && Objects.equals(cleanTitle, that.cleanTitle) && Objects.equals(artistTitle, that.artistTitle) && Objects.equals(albumTitle, that.albumTitle) && Objects.equals(artistTitleInfo, that.artistTitleInfo) && Objects.equals(artistMBId, that.artistMBId) && Objects.equals(albumMBId, that.albumMBId) && Objects.equals(releaseMBId, that.releaseMBId) && Objects.equals(recordingMBId, that.recordingMBId) && Objects.equals(trackMBId, that.trackMBId) && Objects.equals(country, that.country) && Objects.equals(label, that.label) && Objects.equals(catalogNumber, that.catalogNumber) && Objects.equals(disambiguation, that.disambiguation) && Objects.equals(duration, that.duration) && Objects.equals(quality, that.quality) && Objects.equals(mediaInfo, that.mediaInfo) && Objects.equals(trackNumbers, that.trackNumbers) && Objects.equals(releaseGroup, that.releaseGroup) && Objects.equals(releaseHash, that.releaseHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cleanTitle, artistTitle, albumTitle, artistTitleInfo, artistMBId, albumMBId, releaseMBId, recordingMBId, trackMBId, discNumber, discCount, country, year, label, catalogNumber, disambiguation, duration, quality, mediaInfo, trackNumbers, releaseGroup, releaseHash);
    }

    @Override
    public String toString() {
        return "ParsedTrackInfo{" +
                "title='" + title + '\'' +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", artistTitle='" + artistTitle + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", artistTitleInfo=" + artistTitleInfo +
                ", artistMBId='" + artistMBId + '\'' +
                ", albumMBId='" + albumMBId + '\'' +
                ", releaseMBId='" + releaseMBId + '\'' +
                ", recordingMBId='" + recordingMBId + '\'' +
                ", trackMBId='" + trackMBId + '\'' +
                ", discNumber=" + discNumber +
                ", discCount=" + discCount +
                ", country=" + country +
                ", year=" + year +
                ", label='" + label + '\'' +
                ", catalogNumber='" + catalogNumber + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", duration=" + duration +
                ", quality=" + quality +
                ", mediaInfo=" + mediaInfo +
                ", trackNumbers=" + trackNumbers +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", releaseHash='" + releaseHash + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
