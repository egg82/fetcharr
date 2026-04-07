package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.BookFile;
import me.egg82.arr.readarr.v1.schema.AuthorResource;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link BookFile} is fetched from the API for updating.
 */
public class ReadarrFetchBookFilesEvent extends AbstractUpdaterEvent {
    private final BookFile bookFiles;
    private final AuthorResource author;

    public ReadarrFetchBookFilesEvent(@NotNull BookFile bookFiles, @NotNull AuthorResource author, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.bookFiles = bookFiles;
        this.author = author;
    }

    public @NotNull BookFile bookFiles() {
        return bookFiles;
    }

    public @NotNull AuthorResource author() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrFetchBookFilesEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(bookFiles, that.bookFiles) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bookFiles, author);
    }

    @Override
    public String toString() {
        return "ReadarrFetchBookFilesEvent{" +
                "bookFiles=" + bookFiles +
                ", author=" + author +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
