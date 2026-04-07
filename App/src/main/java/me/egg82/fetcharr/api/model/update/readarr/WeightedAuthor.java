package me.egg82.fetcharr.api.model.update.readarr;

import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.arr.readarr.v1.schema.BookResource;
import me.egg82.fetcharr.util.Weighted;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class WeightedAuthor implements Weighted {
    private final AuthorResource author;
    private final List<@NotNull BookResource> books;
    private final Instant latest;

    private Instant lastSelected = Instant.EPOCH;

    public WeightedAuthor(@NotNull AuthorResource author, @NotNull List<@NotNull BookResource> books) {
        this.author = author;
        this.books = books;

        Instant latest = Instant.EPOCH;
        for (BookResource b : books) {
            Instant t = b.lastSearchTime();
            if (t != null && t.isAfter(latest)) {
                latest = t;
            }
        }
        this.latest = latest;
    }

    @Override
    public @NotNull Instant lastUpdated() {
        return this.latest;
    }

    @Override
    public @NotNull Instant lastSelected() {
        return this.lastSelected;
    }

    @Override
    public void lastSelectedNow() {
        this.lastSelected = Instant.now();
    }

    public @NotNull AuthorResource author() {
        return author;
    }

    public @NotNull List<@NotNull BookResource> books() {
        return books;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeightedAuthor that)) return false;
        return Objects.equals(author, that.author) && Objects.equals(books, that.books) && Objects.equals(latest, that.latest) && Objects.equals(lastSelected, that.lastSelected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, books, latest, lastSelected);
    }

    @Override
    public String toString() {
        return "WeightedAuthor{" +
                "author=" + author +
                ", books=" + books +
                ", latest=" + latest +
                ", lastSelected=" + lastSelected +
                '}';
    }
}
