package me.egg82.arr.common;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.file.FetchableAPIObjectMeta;
import me.egg82.arr.file.JSONFile;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractArrAPI implements ArrAPI {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConcurrentMap<Class<? extends FetchableAPIObject>, @Nullable Constructor<?>> constructors = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<? extends FetchableAPIObject>, @Nullable Object> unknowns = new ConcurrentHashMap<>();

    private final ExpiringMap<Pair<Class<? extends FetchableAPIObject>, String>, @Nullable Object> cache = ExpiringMap.builder().expirationPolicy(ExpirationPolicy.CREATED).variableExpiration().build();
    private final ExpiringMap<Pair<ObjectIntPair<Class<? extends FetchableAPIObject>>, String>, @Nullable Object> idCache = ExpiringMap.builder().expirationPolicy(ExpirationPolicy.CREATED).variableExpiration().build();

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
        Pair<Class<? extends FetchableAPIObject>, String> key = Pair.of(type, encode(params));
        T r = (T) cache.computeIfAbsent(key, k -> {
            T cache = getCache(type);
            if (cache != null) {
                logger.debug("Loaded {} from cache ({}_{})", type.getSimpleName(), this.type(), this.id);
                return cache;
            }

            T unknown = buildUnknown(type);
            if (unknown == null) {
                return null;
            }
            JsonNode node = get(unknown.apiPath(), params);
            if (node == null) {
                return null;
            }

            T composite = build(type, node, Instant.now());
            logger.debug("Fetched {} from API ({}_{})", type.getSimpleName(), this.type(), this.id);
            writeCache(composite);
            return composite;
        });
        if (r != null) {
            Duration expires = r.expiresIn();
            cache.setExpiration(key, expires.toMillis(), TimeUnit.MILLISECONDS);
        }
        return r;
    }

    @Override
    public <T extends FetchableAPIObject> @Nullable T fetch(@NotNull Class<T> type, int id, @Nullable Map<String, @NotNull Object> params) {
        Pair<ObjectIntPair<Class<? extends FetchableAPIObject>>, String> key = Pair.of(ObjectIntPair.of(type, id), encode(params));
        T r = (T) idCache.computeIfAbsent(key, k -> {
            T cache = getCache(type, id);
            if (cache != null) {
                logger.debug("Loaded {} ({}) from cache ({}_{})", type.getSimpleName(), id, this.type(), this.id);
                return cache;
            }

            T unknown = buildUnknown(type);
            if (unknown == null) {
                return null;
            }
            JsonNode node = get(unknown.apiPath() + "/" + id, params);
            if (node == null) {
                return null;
            }

            T composite = build(type, node, Instant.now());
            logger.debug("Fetched {} ({}) from API ({}_{})", type.getSimpleName(), id, this.type(), this.id);
            writeCache(composite, id);
            return composite;
        });
        if (r != null) {
            Duration expires = r.expiresIn();
            idCache.setExpiration(key, expires.toMillis(), TimeUnit.MILLISECONDS);
        }
        return r;
    }

    @Override
    public void invalidate(@NotNull Class<? extends FetchableAPIObject> type, @Nullable Map<String, @NotNull Object> params) {
        cache.remove(Pair.of(type, encode(params)));
    }

    @Override
    public void invalidate(@NotNull Class<? extends FetchableAPIObject> type, int id, @Nullable Map<String, @NotNull Object> params) {
        idCache.remove(Pair.of(ObjectIntPair.of(type, id), encode(params)));
    }

    private <T extends FetchableAPIObject> @Nullable T build(@NotNull Class<T> type, @NotNull JsonNode node, @NotNull Instant lastFetched) {
        Constructor<?> c = constructors.computeIfAbsent(type, k -> {
            try {
                return type.getConstructor(ArrAPI.class, JsonNode.class, Instant.class);
            } catch (NoSuchMethodException ex) {
                logger.error("Could not find API constructor for class {}", type.getName(), ex);
            }
            return null;
        });

        try {
            return (T) c.newInstance(this, node, lastFetched);
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

    private <T extends FetchableAPIObject> @Nullable T getCache(@NotNull Class<T> type) {
        T unknown = buildUnknown(type);
        if (unknown == null) {
            return null;
        }

        FetchableAPIObjectMeta metaFile = new FetchableAPIObjectMeta(new JSONFile(new File(getBasePath(type), "meta.json")));
        if (Instant.now().isAfter(metaFile.lastFetched().plus(unknown.cacheTime().duration()))) {
            return null;
        }

        JSONFile cacheFile = new JSONFile(new File(getBasePath(type), "base.json"));
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            return build(type, cacheFile.read(), metaFile.lastFetched());
        } catch (IOException ex) {
            logger.warn("Could not read cache file {}", cacheFile.absolutePath(), ex);
        }
        return null;
    }

    private <T extends FetchableAPIObject> void writeCache(@NotNull T composite) {
        JSONFile cacheFile = new JSONFile(new File(getBasePath(composite.getClass()), "base.json"));
        try {
            cacheFile.write(composite.node());
        }  catch (IOException ex) {
            logger.warn("Could not write cache file {}", cacheFile.absolutePath(), ex);
            return;
        }

        FetchableAPIObjectMeta metaFile = new FetchableAPIObjectMeta(new JSONFile(new File(getBasePath(composite.getClass()), "meta.json")));
        metaFile.setFetched(composite.lastFetched());
        metaFile.write();
    }

    private <T extends FetchableAPIObject> @Nullable T getCache(@NotNull Class<T> type, int id) {
        T unknown = buildUnknown(type);
        if (unknown == null) {
            return null;
        }

        FetchableAPIObjectMeta metaFile = new FetchableAPIObjectMeta(new JSONFile(new File(getBasePath(type), id + ".meta.json")));
        if (Instant.now().isAfter(metaFile.lastFetched().plus(unknown.cacheTime().duration()))) {
            return null;
        }

        JSONFile cacheFile = new JSONFile(new File(getBasePath(type), id + ".json"));
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            return build(type, cacheFile.read(), metaFile.lastFetched());
        } catch (IOException ex) {
            logger.warn("Could not read cache file {}", cacheFile.absolutePath(), ex);
        }
        return null;
    }

    private <T extends FetchableAPIObject> void writeCache(@NotNull T composite, int id) {
        JSONFile cacheFile = new JSONFile(new File(getBasePath(composite.getClass()), id + ".json"));
        try {
            cacheFile.write(composite.node());
        }  catch (IOException ex) {
            logger.warn("Could not write cache file {}", cacheFile.absolutePath(), ex);
            return;
        }

        FetchableAPIObjectMeta metaFile = new FetchableAPIObjectMeta(new JSONFile(new File(getBasePath(composite.getClass()), id + ".meta.json")));
        metaFile.setFetched(composite.lastFetched());
        metaFile.write();
    }

    private <T extends FetchableAPIObject> @NotNull File getBasePath(@NotNull Class<T> type) {
        File base = CacheConfigVars.getFile(CacheConfigVars.CACHE_DIR);
        File arr = new File(base, this.type().name().toLowerCase() + "-" + this.id);
        return new File(arr, type.getSimpleName());
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

    private @NotNull String encode(@Nullable Map<String, @NotNull Object> params) {
        if (params == null) {
            return "";
        }

        // Source - https://stackoverflow.com/a/29213105
        // Posted by eclipse, modified by community. See post 'Timeline' for change history
        // Retrieved 2026-03-17, License - CC BY-SA 3.0
        return params.entrySet().stream()
                .map(p -> URLEncoder.encode(p.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(p.getValue().toString(), StandardCharsets.UTF_8))
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
    }
}
