package me.egg82.arr.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ArrAPI {
    default <T extends FetchableAPIObject> T fetch(@NotNull Class<T> type) {
        return fetch(type, null);
    }
    <T extends FetchableAPIObject> T fetch(@NotNull Class<T> type, @Nullable Map<String, @NotNull Object> params);

    default <T extends FetchableAPIObject> T fetch(@NotNull Class<T> type, int id) {
        return fetch(type, id, null);
    }
    <T extends FetchableAPIObject> T fetch(@NotNull Class<T> type, int id, @Nullable Map<String, @NotNull Object> params);
}
