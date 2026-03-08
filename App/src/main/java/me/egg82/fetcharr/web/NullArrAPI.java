package me.egg82.fetcharr.web;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.egg82.fetcharr.web.model.common.APIObject;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class NullArrAPI implements ArrAPI {
    public static final NullArrAPI INSTANCE = new NullArrAPI();

    private final Cache<Class<? extends APIObject<?>>, Object> unknowns = Caffeine.newBuilder().build();

    private NullArrAPI() { }

    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public int id() {
        return -1;
    }

    @Override
    public @NotNull String baseUrl() {
        return "";
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.UNKNOWN;
    }

    @Override
    public <T extends APIObject<T>> void update(@NonNull T apiObject, boolean force) {

    }

    @Override
    public @NonNull <T extends APIObject<T>> T fetch(Class<T> clazz, boolean lazy) {
        return (T) unknowns.get(clazz, k -> {
            try {
                return k.getField("UNKNOWN").get(null);
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("Could not find UNKNOWN field for class " + k.getName(), ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Could not get UNKNOWN for class " + k.getName(), ex);
            }
        });
    }

    @Override
    public @NonNull <T extends APIObject<T>> T fetch(Class<T> clazz, int id, boolean lazy) {
        return (T) unknowns.get(clazz, k -> {
            try {
                return k.getField("UNKNOWN").get(null);
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("Could not find UNKNOWN field for class " + k.getName(), ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Could not get UNKNOWN for class " + k.getName(), ex);
            }
        });
    }

    @Override
    public void search(int... itemIds) {

    }
}
