package me.egg82.arr.common;

import it.unimi.dsi.fastutil.ints.IntCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ArrAPI {
    boolean valid();

    int id();
    @NotNull String baseUrl();
    @NotNull ArrType type();
    @NotNull String version();

    default <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type) {
        return fetch(type, null);
    }
    <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, @Nullable Map<String, @NotNull Object> params);

    default <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, int id) {
        return fetch(type, id, null);
    }
    <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, int id, @Nullable Map<String, @NotNull Object> params);

    default void invalidate(@NotNull Class<? extends FetchableAPIObject> type) {
        invalidate(type, null);
    }
    void invalidate(@NotNull Class<? extends FetchableAPIObject> type, @Nullable Map<String, @NotNull Object> params);

    default void invalidate(@NotNull Class<? extends FetchableAPIObject> type, int id) {
        invalidate(type, id, null);
    }
    void invalidate(@NotNull Class<? extends FetchableAPIObject> type, int id, @Nullable Map<String, @NotNull Object> params);

    default void search(int itemId) {
        search(new int[] { itemId });
    }
    default void search(@NotNull IntCollection ids) {
        search(ids.toIntArray());
    }
    void search(int... itemIds);
}
