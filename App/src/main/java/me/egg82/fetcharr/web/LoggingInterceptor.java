package me.egg82.fetcharr.web;

import kong.unirest.core.Interceptor;
import me.egg82.fetcharr.env.LogMode;
import org.jetbrains.annotations.NotNull;

public class LoggingInterceptor implements Interceptor {
    private final LogMode mode;

    public LoggingInterceptor(@NotNull LogMode mode) {
        this.mode = mode;
    }
}
