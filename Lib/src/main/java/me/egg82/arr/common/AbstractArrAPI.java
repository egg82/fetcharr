package me.egg82.arr.common;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.egg82.arr.unit.TimeValue;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractArrAPI implements ArrAPI {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConcurrentMap<Class<? extends FetchableAPIObject>, @Nullable Constructor<?>> constructors = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<? extends FetchableAPIObject>, @Nullable Object> unknowns = new ConcurrentHashMap<>();

    private final ExpiringMap<Class<? extends FetchableAPIObject>, @Nullable Object> cache = ExpiringMap.builder().expirationPolicy(ExpirationPolicy.CREATED).variableExpiration().build();
    private final ExpiringMap<ObjectIntPair<Class<? extends FetchableAPIObject>>, @Nullable Object> idCache = ExpiringMap.builder().expirationPolicy(ExpirationPolicy.CREATED).variableExpiration().build();

    protected final String baseUrl;
    protected final String apiKey;
    private final int id;

    public AbstractArrAPI(@NotNull String baseUrl, @NotNull String apiKey, int id) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public @NotNull String baseUrl() {
        return this.baseUrl;
    }

    @Override
    public <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, @Nullable Map<String, @NotNull Object> params) {
        T r = (T) cache.computeIfAbsent(type, k -> {
            T unknown = buildUnknown(type);
            if (unknown == null) {
                return null;
            }
            JsonNode node = get(unknown.apiPath(), params);
            if (node == null) {
                return null;
            }
            return build(type, node);
        });
        if (r != null) {
            TimeValue time = r.cacheTime();
            cache.setExpiration(type, time.time(), time.unit());
        }
        return r;
    }

    @Override
    public <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, int id, @Nullable Map<String, @NotNull Object> params) {
        T r = (T) idCache.computeIfAbsent(ObjectIntPair.of(type, id), k -> {
            T unknown = buildUnknown(type);
            if (unknown == null) {
                return null;
            }
            JsonNode node = get(unknown.apiPath() + "/" + id, params);
            if (node == null) {
                return null;
            }
            return build(type, node);
        });
        if (r != null) {
            TimeValue time = r.cacheTime();
            cache.setExpiration(type, time.time(), time.unit());
        }
        return r;
    }

    @Override
    public void invalidate(@NotNull Class<? extends FetchableAPIObject> type) {
        cache.remove(type);
    }

    @Override
    public void invalidate(@NotNull Class<? extends FetchableAPIObject> type, int id) {
        idCache.remove(ObjectIntPair.of(type, id));
    }

    private <T extends FetchableAPIObject> @Nullable T build(@NotNull Class<T> type, @NotNull JsonNode node) {
        Constructor<?> c = constructors.computeIfAbsent(type, k -> {
            try {
                return type.getConstructor(ArrAPI.class, JsonNode.class);
            } catch (NoSuchMethodException ex) {
                logger.error("Could not find API constructor for class {}", type.getName(), ex);
            }
            return null;
        });

        try {
            return (T) c.newInstance(this, node);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            logger.error("Could not create new instance of class {}", type.getName(), ex);
        }
        return null;
    }

    private <T extends FetchableAPIObject> @Nullable T buildUnknown(@NotNull Class<T> type) {
        return (T) unknowns.computeIfAbsent(type, k -> {
            try {
                return type.getField("UNKNOWN").get(null);
            } catch (NoSuchFieldException ex) {
                logger.error("Could not find UNKNOWN field for class {}", type.getName(), ex);
            } catch (IllegalAccessException ex) {
                logger.error("Could not get UNKNOWN for class {}", type.getName(), ex);
            }
            return null;
        });
    }

    protected final @Nullable JsonNode get(@NotNull String apiPath) {
        return get(apiPath, null);
    }

    protected final @Nullable JsonNode get(@NotNull String apiPath, @Nullable Map<String, @NotNull Object> params) {
        return parseResponse(Unirest.get(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .accept("application/json")
                .queryString(params)
                .asJson());
    }

    protected final @Nullable JsonNode put(@NotNull String apiPath) {
        return put(apiPath, null);
    }

    protected final @Nullable JsonNode put(@NotNull String apiPath, @Nullable JsonNode body) {
        return parseResponse(Unirest.put(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    protected final @Nullable JsonNode post(@NotNull String apiPath) {
        return post(apiPath, null);
    }

    protected final @Nullable JsonNode post(@NotNull String apiPath, @Nullable JsonNode body) {
        return parseResponse(Unirest.post(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    protected final @Nullable JsonNode delete(@NotNull String apiPath) {
        return delete(apiPath, null);
    }

    protected final @Nullable JsonNode delete(@NotNull String apiPath, @Nullable JsonNode body) {
        return parseResponse(Unirest.delete(baseUrl + apiPath)
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(body)
                .asJson());
    }

    private @Nullable JsonNode parseResponse(@NotNull HttpResponse<JsonNode> response) {
        if (!response.isSuccess()) {
            logger.warn("Got non-success response (code {}) for URL {}", response.getStatus(), response.getRequestSummary().getUrl());
            response.getParsingError().ifPresent(v -> logger.warn("JSON parsing error for URL {}", response.getRequestSummary().getUrl(), v));
            return null;
        }

        if (response.getBody() == null) {
            logger.warn("JSON body was null for URL {}", response.getRequestSummary().getUrl());
        }

        return response.getBody();
    }
}
