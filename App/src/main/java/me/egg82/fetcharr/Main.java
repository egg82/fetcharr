package me.egg82.fetcharr;

import kong.unirest.core.Proxy;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.LogMode;
import me.egg82.fetcharr.env.RadarrConfigVars;
import me.egg82.fetcharr.env.SonarrConfigVars;
import me.egg82.fetcharr.web.LoggingInterceptor;
import me.egg82.fetcharr.web.radarr.RadarrAPI;
import me.egg82.fetcharr.web.sonarr.SonarrAPI;
import me.egg82.fetcharr.work.radarr.RadarrUpdater;
import me.egg82.fetcharr.work.sonarr.SonarrUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", ConfigVars.getLogMode(ConfigVars.LOG_MODE).name().toLowerCase());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final ScheduledExecutorService workPool = Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2));
    private static final List<Runnable> radarr = new ArrayList<>();
    private static final List<Runnable> sonarr = new ArrayList<>();

    public static void main(String[] args) {
        LOGGER.info("Starting..");
        LOGGER.info("Logging mode set to {}", ConfigVars.getLogMode(ConfigVars.LOG_MODE).name());
        LOGGER.info("Thread pool size set to {}", Math.max(4, Runtime.getRuntime().availableProcessors() / 2));

        setupUnirest();

        for (int i = 0; i < 100; i++) {
            setupRadarr(i);
            setupSonarr(i);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down..");
            workPool.shutdown();
            try {
                if (!workPool.awaitTermination(10L, TimeUnit.SECONDS)) {
                    workPool.shutdownNow();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }));

        while (true) {
            try {
                Thread.sleep(5_000);

                for (Runnable r : radarr) {
                    workPool.submit(r);
                }
                for (Runnable r : sonarr) {
                    workPool.submit(r);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void setupUnirest() {
        LogMode logMode = ConfigVars.getLogMode(ConfigVars.LOG_MODE);
        Unirest.config().interceptor(new LoggingInterceptor(logMode));

        String proxyHost = ConfigVars.get(ConfigVars.PROXY_HOST);
        int proxyPort = ConfigVars.getInt(ConfigVars.PROXY_PORT);
        if (proxyHost != null && proxyPort > 0) {
            Unirest.config().proxy(new Proxy(proxyHost, proxyPort));
        }

        int connectTimeout = ConfigVars.getInt(ConfigVars.CONNECT_TIMEOUT);
        if (connectTimeout > 0) {
            Unirest.config().connectTimeout(connectTimeout);
        }

        int requestTimeout = ConfigVars.getInt(ConfigVars.REQUEST_TIMEOUT);
        if (requestTimeout > 0) {
            Unirest.config().requestTimeout(requestTimeout);
        }

        int connectTTL = ConfigVars.getInt(ConfigVars.CONNECT_TTL);
        if (connectTTL > -1) {
            Unirest.config().connectionTTL(connectTTL, TimeUnit.MILLISECONDS);
        }

        boolean verifyCerts = ConfigVars.getBool(ConfigVars.VERIFY_CERTS);
        Unirest.config().disableHostNameVerification(!verifyCerts);
        Unirest.config().verifySsl(verifyCerts);

        File certsPath = ConfigVars.getFile(ConfigVars.SSL_PATH);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certs = null;
            try (FileInputStream stream = new FileInputStream(certsPath)) {
                certs = cf.generateCertificates(stream);
            }
            if (certs != null && !certs.isEmpty()) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                int i = 0;
                for (Certificate cert : certs) {
                    trustStore.setCertificateEntry("cert-" + i, cert);
                    i++;
                }

                TrustManagerFactory tf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tf.init(trustStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tf.getTrustManagers(), new SecureRandom());
                Unirest.config().sslContext(context);

                LOGGER.info("Loaded {} SSL certs", i);
            } else {
                LOGGER.warn("No SSL certs found at {}", certsPath.getAbsolutePath());
            }
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException |
                 KeyManagementException ex) {
            LOGGER.warn("Could not load SSL certs at {}", certsPath.getAbsolutePath(), ex);
        }

        Unirest.config().retryAfter(true);
    }

    private static void setupRadarr(int num) {
        String url = RadarrConfigVars.get(RadarrConfigVars.URL, num);
        String key = RadarrConfigVars.get(RadarrConfigVars.API_KEY, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Radarr URL at {} missing", RadarrConfigVars.URL.envName(num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Radarr API key at {} missing", RadarrConfigVars.API_KEY.envName(num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        RadarrAPI api = new RadarrAPI(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Radarr instance configured at {} ({})", RadarrConfigVars.URL.envName(num), url);
            return;
        }

        radarr.add(new RadarrUpdater(api));
        LOGGER.info("Added Radarr instance at {}", url);
    }

    private static void setupSonarr(int num) {
        String url = SonarrConfigVars.get(SonarrConfigVars.URL, num);
        String key = SonarrConfigVars.get(SonarrConfigVars.API_KEY, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Sonarr URL at {} missing", SonarrConfigVars.URL.envName(num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Sonarr API key at {} missing", SonarrConfigVars.API_KEY.envName(num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        SonarrAPI api = new SonarrAPI(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Sonarr instance configured at {} ({})", SonarrConfigVars.URL.envName(num), url);
            return;
        }

        sonarr.add(new SonarrUpdater(api));
        LOGGER.info("Added Sonarr instance at {}", url);
    }
}
