package me.egg82.fetcharr.web.whisparr;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import me.egg82.fetcharr.web.AbstractArrAPI;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.ArrType;
import me.egg82.fetcharr.web.model.common.APIObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class WhisparrAPI extends AbstractArrAPI {
    private final Cache<Class<? extends APIObject<?>>, Constructor<?>> constructors = Caffeine.newBuilder().build();
    private final Cache<Class<? extends APIObject<?>>, Object> unknowns = Caffeine.newBuilder().build();

    private final Cache<Class<? extends APIObject<?>>, Object> cache = Caffeine.newBuilder().build();
    private final Cache<Class<? extends APIObject<?>>, Int2ObjectMap<Object>> idCache = Caffeine.newBuilder().build();

    public WhisparrAPI(@NotNull String baseUrl, @NotNull String apiKey, int id) {
        super(baseUrl, apiKey, id);
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.WHISPARR;
    }

    @Override
    public @NotNull String version() {
        return "v3";
    }

    @Override
    public boolean valid() {
        JsonNode response = get("/api");
        if (response == null) {
            logger.warn("Whisparr returned invalid response for URL {}: null", baseUrl + "/api");
            return false;
        }
        String current = response.getObject().getString("current");
        if (current == null || !current.equalsIgnoreCase(version())) {
            logger.warn("Whisparr returned unexpected response for URL {}: {}", baseUrl + "/api", response.getObject().toString());
            return false;
        }
        return true;
    }

    @Override
    public <T extends APIObject<T>> void update(@NonNull T apiObject, boolean force) {
        if (force || (!apiObject.valid() && !apiObject.unknown())) {
            apiObject.fetch(apiKey);
        }
    }

    @Override
    public @NotNull <T extends APIObject<T>> T fetch(Class<T> clazz, boolean lazy) {
        T r = (T) cache.get(clazz, k -> fetchInternal(clazz));
        if (!lazy && !r.valid() && !r.unknown()) {
            r.fetch(apiKey);
        }
        return r;
    }

    private @NotNull <T extends APIObject<T>> T fetchInternal(Class<T> clazz) {
        try {
            return (T) constructors.get(clazz, k -> {
                try {
                    return k.getConstructor(ArrAPI.class);
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException("Could not find API constructor for class " + k.getName(), ex);
                }
            }).newInstance(this);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            logger.error("Could not instantiate new {}", clazz.getName(), ex);
            return (T) unknowns.get(clazz, k -> {
                try {
                    return k.getField("UNKNOWN").get(null);
                } catch (NoSuchFieldException ex2) {
                    throw new RuntimeException("Could not find UNKNOWN field for class " + k.getName(), ex2);
                } catch (IllegalAccessException ex2) {
                    throw new RuntimeException("Could not get UNKNOWN for class " + k.getName(), ex2);
                }
            });
        }
    }

    @Override
    public @NotNull <T extends APIObject<T>> T fetch(Class<T> clazz, int id, boolean lazy) {
        Int2ObjectMap<Object> map = idCache.get(clazz, k -> new Int2ObjectArrayMap<>());
        T r = (T) map.computeIfAbsent(id, k -> fetchInternal(clazz, k));
        if (!lazy && !r.valid() && !r.unknown()) {
            r.fetch(apiKey);
        }
        return r;
    }

    private @NotNull <T extends APIObject<T>> T fetchInternal(Class<T> clazz, int id) {
        try {
            return (T) constructors.get(clazz, k -> {
                try {
                    return k.getConstructor(ArrAPI.class, int.class);
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException("Could not find API constructor for class " + k.getName(), ex);
                }
            }).newInstance(this, id);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            logger.error("Could not instantiate new {}", clazz.getName(), ex);
            return (T) unknowns.get(clazz, k -> {
                try {
                    return k.getField("UNKNOWN").get(null);
                } catch (NoSuchFieldException ex2) {
                    throw new RuntimeException("Could not find UNKNOWN field for class " + k.getName(), ex2);
                } catch (IllegalAccessException ex2) {
                    throw new RuntimeException("Could not get UNKNOWN for class " + k.getName(), ex2);
                }
            });
        }
    }

    @Override
    public void search(int... itemIds) {
        JSONObject data = new JSONObject(Map.of(
                "movieIds", itemIds,
                "name", "MoviesSearch"
        ));
        JsonNode response = post("/api/" + version() + "/command", new JsonNode(data.toString()));
        if (response == null) {
            logger.warn("Whisparr returned invalid response for URL {}: null", baseUrl + "/api/" + version() + "/command");
            return;
        }
        int id = NumberParser.parseInt(-1, StringParser.parse(response.getObject(), "id"));
        if (id < 0) {
            logger.warn("Whisparr returned unexpected response for URL {}: {}", baseUrl + "/api/" + version() + "/command", response.getObject().toString());
        }
    }
}
