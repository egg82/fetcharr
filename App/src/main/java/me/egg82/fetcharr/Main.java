package me.egg82.fetcharr;

import kong.unirest.core.Proxy;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.LogMode;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.web.LoggingInterceptor;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", LogMode.getMode(ConfigVars.getVar(ConfigVars.LOG_MODE), ConfigVars.LOG_MODE.def()).name().toLowerCase());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final List<RadarrAPI> radarr = new ArrayList<>();

    public static void main(String[] args) {
        LOGGER.info("Logging mode set to {}", LogMode.getMode(ConfigVars.getVar(ConfigVars.LOG_MODE), ConfigVars.LOG_MODE.def()).name());

        setupUnirest();

        for (int i = 0; i < 100; i++) {
            setupRadarr(i);
        }
    }

    private static void setupUnirest() {
        LogMode logMode = LogMode.getMode(ConfigVars.getVar(ConfigVars.LOG_MODE), ConfigVars.LOG_MODE.def());
        Unirest.config().interceptor(new LoggingInterceptor(logMode));

        String proxyHost = ConfigVars.getVar(ConfigVars.PROXY_HOST);
        int proxyPort = ConfigVars.getVar(ConfigVars.PROXY_PORT, (int) ConfigVars.PROXY_PORT.def());
        if (proxyHost != null && proxyPort > 0) {
            Unirest.config().proxy(new Proxy(proxyHost, proxyPort));
        }

        int connectTimeout = ConfigVars.getVar(ConfigVars.CONNECT_TIMEOUT, (int) ConfigVars.CONNECT_TIMEOUT.def());
        if (connectTimeout > 0) {
            Unirest.config().connectTimeout(connectTimeout);
        }

        int requestTimeout = ConfigVars.getVar(ConfigVars.REQUEST_TIMEOUT, (int) ConfigVars.REQUEST_TIMEOUT.def());
        if (requestTimeout > 0) {
            Unirest.config().requestTimeout(requestTimeout);
        }

        int connectTTL = ConfigVars.getVar(ConfigVars.CONNECT_TTL, (int) ConfigVars.CONNECT_TTL.def());
        if (connectTTL > -1) {
            Unirest.config().connectionTTL(connectTTL, TimeUnit.MILLISECONDS);
        }

        boolean verifyCerts = ConfigVars.getVar(ConfigVars.VERIFY_CERTS, (boolean) ConfigVars.VERIFY_CERTS.def());
        Unirest.config().disableHostNameVerification(!verifyCerts);
        Unirest.config().verifySsl(verifyCerts);

        Unirest.config().retryAfter(true);
    }

    private static void setupRadarr(int num) {
        String url = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_URL, num);
        String key = RadarrConfigVars.getVar(RadarrConfigVars.RADARR_API_KEY, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Radarr URL at {} missing", RadarrConfigVars.RADARR_URL.name(num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Radarr API key at {} missing", RadarrConfigVars.RADARR_API_KEY.name(num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        RadarrAPI api = new RadarrAPI(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Radarr instance configured at {} ({})", RadarrConfigVars.RADARR_URL.name(num), url);
            return;
        }

        radarr.add(api);
        LOGGER.info("Added Radarr instance at {}", url);

        api.movies(); // TODO: Remove
    }
}
