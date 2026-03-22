package me.egg82.fetcharr.api;

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
            REGISTER = FetcharrAPIProvider.class.getDeclaredMethod("register", FetcharrAPI.class);
            REGISTER.setAccessible(true);

            DEREGISTER = FetcharrAPIProvider.class.getDeclaredMethod("deregister");
            DEREGISTER.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private APIRegistrationUtil() { }

    public static void register(@NotNull FetcharrAPI api) {
        try {
            REGISTER.invoke(null, api);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            LOGGER.error("Could not invoke FetcharrAPIProvider.register(FetcharrAPI)", ex);
        }
    }

    public static void deregister() {
        try {
            DEREGISTER.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            LOGGER.error("Could not invoke FetcharrAPIProvider.deregister()", ex);
        }
    }
}
