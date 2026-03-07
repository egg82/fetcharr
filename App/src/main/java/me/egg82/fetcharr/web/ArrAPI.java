package me.egg82.fetcharr.web;

import it.unimi.dsi.fastutil.ints.IntCollection;
import me.egg82.fetcharr.web.model.common.*;
import org.jetbrains.annotations.NotNull;

public interface ArrAPI {
    ArrAPI UNKNOWN = NullArrAPI.INSTANCE;

    boolean valid();

    int id();
    @NotNull String baseUrl();
    @NotNull ArrType type();

    @NotNull <T extends APIObject<T>> T fetch(Class<T> clazz);

    @NotNull <T extends APIObject<T>> T fetch(Class<T> clazz, int id);

    default void search(int itemId) { search(new int[] { itemId }); }
    default void search(IntCollection ids) { search(ids.toIntArray()); }
    void search(int... itemIds);
}
