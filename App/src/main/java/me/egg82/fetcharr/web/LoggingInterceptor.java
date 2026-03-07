package me.egg82.fetcharr.web;

import kong.unirest.core.*;
import me.egg82.fetcharr.env.LogMode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    private final LogMode mode;

    public LoggingInterceptor(@NotNull LogMode mode) {
        this.mode = mode;
    }

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
        LOGGER.debug("HTTP request to {} sent", request.getUrl());
    }

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        LOGGER.debug("Response from {} received: {}", request.getUrl(), response.getStatus());
    }

    @Override
    public HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config) throws UnirestException {
        LOGGER.debug("Failed response from {} received", request.getUrl(), e);
        throw new UnirestException(e);
    }
}
