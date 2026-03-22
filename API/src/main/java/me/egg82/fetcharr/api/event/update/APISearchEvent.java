package me.egg82.fetcharr.api.event.update;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class APISearchEvent extends AbstractCancellableUpdaterEvent {
    private IntList ids;

    public APISearchEvent(@NotNull IntList ids, @NotNull Updater updater, @NotNull FetcharrAPI api) {
        super(updater, api);

        this.ids = new IntArrayList(ids);
    }

    public @NotNull IntList ids() {
        return ids;
    }

    public void ids(@NotNull IntList ids) {
        this.ids = ids;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof APISearchEvent that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ids);
    }

    @Override
    public String toString() {
        return "RadarrSearchEvent{" +
                "ids=" + ids +
                ", updater=" + updater +
                ", api=" + api +
                '}';
    }
}
