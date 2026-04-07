package me.egg82.fetcharr.api.event.update.readarr;

import me.egg82.arr.readarr.v1.Author;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.event.update.AbstractUpdaterEvent;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a {@link Author} is fetched from the API for updating.
 */
public class ReadarrFetchAuthorEvent extends AbstractUpdaterEvent {
    private final Author author;

    public ReadarrFetchAuthorEvent(@NotNull Author author, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.author = author;
    }

    public @NotNull Author author() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReadarrFetchAuthorEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), author);
    }

    @Override
    public String toString() {
        return "ReadarrFetchAuthorEvent{" +
                "author=" + author +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
