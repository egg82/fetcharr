package me.egg82.arr.parse;

import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.APIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.log.FileLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ObjectParser {
    private static final Logger LOGGER = new FileLogger(LoggerFactory.getLogger(ObjectParser.class));

    private static final ConcurrentMap<Class<? extends APIObject>, @Nullable Constructor<?>> constructors = new ConcurrentHashMap<>();

    public static <T extends APIObject> @NotNull T get(@NotNull T def, @NotNull Class<T> type, @NotNull ArrAPI api, @Nullable JSONObject obj, @Nullable String key) {
        return get(def, type, api, obj, key, false);
    }

    public static <T extends APIObject> @NotNull T get(@NotNull T def, @NotNull Class<T> type, @NotNull ArrAPI api, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        T r = get(type, api, obj, key, silent);
        return r != null ? r : def;
    }

    public static <T extends APIObject> @Nullable T get(@NotNull Class<T> type, @NotNull ArrAPI api, @Nullable JSONObject obj, @Nullable String key) {
        return get(type, api, obj, key, false);
    }

    public static <T extends APIObject> @Nullable T get(@NotNull Class<T> type, @NotNull ArrAPI api, @Nullable JSONObject obj, @Nullable String key, boolean silent) {
        if (obj == null || key == null || key.isEmpty() || !obj.has(key)) {
            return null;
        }
        JSONObject val;
        try {
            val = obj.getJSONObject(key);
        } catch (JSONException ex) {
            if (!silent) {
                LOGGER.warn("Could not convert \"{}\" to JSONObject", key, ex);
            }
            return null;
        }
        return build(type, api, val, silent);
    }

    private static <T extends APIObject> @Nullable T build(@NotNull Class<T> type, @NotNull ArrAPI api, @NotNull JSONObject val, boolean silent) {
        Constructor<?> c = constructors.computeIfAbsent(type, k -> {
            try {
                return type.getConstructor(ArrAPI.class, JSONObject.class);
            } catch (NoSuchMethodException ex) {
                if (!silent) {
                    LOGGER.error("Could not find API constructor for class {}", type.getName(), ex);
                }
            }
            return null;
        });

        try {
            return (T) c.newInstance(api, val);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            if (!silent) {
                LOGGER.error("Could not create new instance of class {}", type.getName(), ex);
            }
        }
        return null;
    }

    private ObjectParser() { }
}
