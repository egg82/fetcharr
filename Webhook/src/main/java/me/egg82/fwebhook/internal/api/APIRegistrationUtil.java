package me.egg82.fwebhook.internal.api;

import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.api.WebhookAPIProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class APIRegistrationUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIRegistrationUtil.class);

    private static final Method REGISTER;
    private static final Method DEREGISTER;

    static {
        try {
            REGISTER = WebhookAPIProvider.class.getDeclaredMethod("register", WebhookAPI.class);
            REGISTER.setAccessible(true);

            DEREGISTER = WebhookAPIProvider.class.getDeclaredMethod("deregister");
            DEREGISTER.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private APIRegistrationUtil() { }

    public static void register(@NotNull WebhookAPI api) {
        try {
            REGISTER.invoke(null, api);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            LOGGER.error("Could not invoke WebhookAPIProvider.register(WebhookAPI)", ex);
        }
    }

    public static void deregister() {
        try {
            DEREGISTER.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            LOGGER.error("Could not invoke WebhookAPIProvider.deregister()", ex);
        }
    }
}
