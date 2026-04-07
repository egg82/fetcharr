package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.Book;
import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Book} is fetched from the API for updating.
 */
public class ReadarrFetchBooksEvent extends AbstractUpdaterEvent {
    private final Book books;
    private final AuthorResource author;

    public ReadarrFetchBooksEvent(@NotNull Book books, @NotNull AuthorResource author, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.books = books;
        this.author = author;
    }

    public @NotNull Book books() {
        return books;
    }

    public @NotNull AuthorResource author() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrFetchBooksEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(books, that.books) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), books, author);
    }

    @Override
    public String toString() {
        return "ReadarrFetchBooksEvent{" +
                "books=" + books +
                ", author=" + author +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
