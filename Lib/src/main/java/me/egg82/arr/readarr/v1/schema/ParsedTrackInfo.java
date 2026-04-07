package me.egg82.arr.readarr.v1.schema;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import org.pcollections.PSet;
import org.pcollections.TreePSet;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ParsedTrackInfo extends AbstractAPIObject {
    private final String title;
    private final String cleanTitle;
    private final PSet<@NotNull String> authors;
    private final String authorTitle;
    private final String bookTitle;
    private final String seriesTitle;
    private final String seriesIndex;
    private final String isbn;
    private final String asin;
    private final String goodreadsId;
    private final String authorMBId;
    private final String bookMBId;
    private final String releaseMBId;
    private final String recordingMBId;
    private final String trackMBId;
    private final int discNumber;
    private final int discCount;
    private final IsoCountry country;
    private final int year;
    private final String publisher;
    private final String label;
    private final String source;
    private final String catalogNumber;
    private final String disambiguation;
    private final Duration duration;
    private final QualityModel quality;
    private final MediaInfoModel mediaInfo;
    private final IntSet trackNumbers = new IntArraySet();
    private final String language;
    private final String releaseGroup;
    private final String releaseHash;

    public ParsedTrackInfo(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.title = StringParser.get(obj, "title");
        this.cleanTitle = StringParser.get(obj, "cleanTitle");

        JSONArray authors = obj.has("authors") && obj.get("authors") != null ? obj.getJSONArray("authors") : null;
        Set<@NotNull String> authorsL = new HashSet<>();
        if (authors != null) {
            for (int i = 0; i < authors.length(); i++) {
                authorsL.add(authors.getString(i));
            }
        }
        this.authors = TreePSet.from(authorsL);

        this.authorTitle = StringParser.get(obj, "authorTitle");
        this.bookTitle = StringParser.get(obj, "bookTitle");
        this.seriesTitle = StringParser.get(obj, "seriesTitle");
        this.seriesIndex = StringParser.get(obj, "seriesIndex");
        this.isbn = StringParser.get(obj, "isbn");
        this.asin = StringParser.get(obj, "asin");
        this.goodreadsId = StringParser.get(obj, "goodreadsId");
        this.authorMBId = StringParser.get(obj, "authorMBId");
        this.bookMBId = StringParser.get(obj, "bookMBId");
        this.releaseMBId = StringParser.get(obj, "releaseMBId");
        this.recordingMBId = StringParser.get(obj, "recordingMBId");
        this.trackMBId = StringParser.get(obj, "trackMBId");
        this.discNumber = NumberParser.getInt(-1, obj, "discNumber");
        this.discCount = NumberParser.getInt(-1, obj, "discCount");
        this.country = ObjectParser.get(IsoCountry.class, api, obj, "country");
        this.year = NumberParser.getInt(-1, obj, "year");
        this.publisher = StringParser.get(obj, "publisher");
        this.label = StringParser.get(obj, "label");
        this.source = StringParser.get(obj, "source");
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

        this.language = StringParser.get(obj, "language");
        this.releaseGroup = StringParser.get(obj, "releaseGroup");
        this.releaseHash = StringParser.get(obj, "releaseHash");
    }

    public @Nullable String title() {
        return title;
    }

    public @Nullable String cleanTitle() {
        return cleanTitle;
    }

    public @NotNull PSet<@NotNull String> authors() {
        return authors;
    }

    public @Nullable String authorTitle() {
        return authorTitle;
    }

    public @Nullable String bookTitle() {
        return bookTitle;
    }

    public @Nullable String seriesTitle() {
        return seriesTitle;
    }

    public @Nullable String seriesIndex() {
        return seriesIndex;
    }

    public @Nullable String isbn() {
        return isbn;
    }

    public @Nullable String asin() {
        return asin;
    }

    public @Nullable String goodreadsId() {
        return goodreadsId;
    }

    public @Nullable String authorMBId() {
        return authorMBId;
    }

    public @Nullable String bookMBId() {
        return bookMBId;
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

    public @Nullable String publisher() {
        return publisher;
    }

    public @Nullable String label() {
        return label;
    }

    public @Nullable String source() {
        return source;
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

    public @NotNull IntSet trackNumbers() {
        return IntSet.of(trackNumbers.toIntArray());
    }

    public @Nullable String language() {
        return language;
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
        return discNumber == that.discNumber && discCount == that.discCount && year == that.year && Objects.equals(title, that.title) && Objects.equals(cleanTitle, that.cleanTitle) && Objects.equals(authors, that.authors) && Objects.equals(authorTitle, that.authorTitle) && Objects.equals(bookTitle, that.bookTitle) && Objects.equals(seriesTitle, that.seriesTitle) && Objects.equals(seriesIndex, that.seriesIndex) && Objects.equals(isbn, that.isbn) && Objects.equals(asin, that.asin) && Objects.equals(goodreadsId, that.goodreadsId) && Objects.equals(authorMBId, that.authorMBId) && Objects.equals(bookMBId, that.bookMBId) && Objects.equals(releaseMBId, that.releaseMBId) && Objects.equals(recordingMBId, that.recordingMBId) && Objects.equals(trackMBId, that.trackMBId) && Objects.equals(country, that.country) && Objects.equals(publisher, that.publisher) && Objects.equals(label, that.label) && Objects.equals(source, that.source) && Objects.equals(catalogNumber, that.catalogNumber) && Objects.equals(disambiguation, that.disambiguation) && Objects.equals(duration, that.duration) && Objects.equals(quality, that.quality) && Objects.equals(mediaInfo, that.mediaInfo) && Objects.equals(trackNumbers, that.trackNumbers) && Objects.equals(language, that.language) && Objects.equals(releaseGroup, that.releaseGroup) && Objects.equals(releaseHash, that.releaseHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cleanTitle, authors, authorTitle, bookTitle, seriesTitle, seriesIndex, isbn, asin, goodreadsId, authorMBId, bookMBId, releaseMBId, recordingMBId, trackMBId, discNumber, discCount, country, year, publisher, label, source, catalogNumber, disambiguation, duration, quality, mediaInfo, trackNumbers, language, releaseGroup, releaseHash);
    }

    @Override
    public String toString() {
        return "ParsedTrackInfo{" +
                "title='" + title + '\'' +
                ", cleanTitle='" + cleanTitle + '\'' +
                ", authors=" + authors +
                ", authorTitle='" + authorTitle + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", seriesTitle='" + seriesTitle + '\'' +
                ", seriesIndex='" + seriesIndex + '\'' +
                ", isbn='" + isbn + '\'' +
                ", asin='" + asin + '\'' +
                ", goodreadsId='" + goodreadsId + '\'' +
                ", authorMBId='" + authorMBId + '\'' +
                ", bookMBId='" + bookMBId + '\'' +
                ", releaseMBId='" + releaseMBId + '\'' +
                ", recordingMBId='" + recordingMBId + '\'' +
                ", trackMBId='" + trackMBId + '\'' +
                ", discNumber=" + discNumber +
                ", discCount=" + discCount +
                ", country=" + country +
                ", year=" + year +
                ", publisher='" + publisher + '\'' +
                ", label='" + label + '\'' +
                ", source='" + source + '\'' +
                ", catalogNumber='" + catalogNumber + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", duration=" + duration +
                ", quality=" + quality +
                ", mediaInfo=" + mediaInfo +
                ", trackNumbers=" + trackNumbers +
                ", language='" + language + '\'' +
                ", releaseGroup='" + releaseGroup + '\'' +
                ", releaseHash='" + releaseHash + '\'' +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
