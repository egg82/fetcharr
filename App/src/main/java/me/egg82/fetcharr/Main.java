package me.egg82.fetcharr;

import kong.unirest.core.Proxy;
import kong.unirest.core.Unirest;
import me.egg82.arr.radarr.RadarrV3API;
import me.egg82.fetcharr.config.LogConfigVars;
import me.egg82.fetcharr.env.*;
import me.egg82.fetcharr.work.radarr.RadarrUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinylog.configuration.Configuration;

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
        // ChatGPT wrote most of this logging config. Tested, tweaked, and overall looks fine
        File logDir = LogConfigVars.getFile(LogConfigVars.LOG_DIR);
        if (!logDir.exists() && !logDir.mkdirs()) {
            throw new IllegalStateException("Could not create log directory: " + logDir.getAbsolutePath());
        }

        Configuration.set("writer", "console");
        Configuration.set("writer.level", LogConfigVars.getLogMode(LogConfigVars.LOG_MODE).name().toLowerCase());
        Configuration.set("writer.format", "{date: HH:mm:ss.SSS} [{level}] {message}");
        Configuration.set("writer.stream", "err@WARN");

        Configuration.set("writer2", "rolling file");
        Configuration.set("writer2.level", "trace");
        Configuration.set("writer2.format", "{date: yyyy-MM-dd HH:mm:ss.SSS} {class}.{method}() [{level}] {message}");
        Configuration.set("writer2.file", new File(logDir, "fetcharr-{date:yyyy-MM-dd}-{count}.log").getAbsolutePath());
        Configuration.set("writer2.latest", new File(logDir, "fetcharr-latest.log").getAbsolutePath());
        Configuration.set("writer2.charset", "UTF-8");
        Configuration.set("writer2.buffered", "true");
        Configuration.set("writer2.policies", "daily, size: 25mb");
        Configuration.set("writer2.convert", "gzip");
        Configuration.set("writer2.backups", "30");

        Configuration.set("writingthread", "true");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final ScheduledExecutorService workPool = Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2));
    private static final List<Runnable> radarr = new ArrayList<>();
    private static final List<Runnable> sonarr = new ArrayList<>();
    private static final List<Runnable> lidarr = new ArrayList<>();
    private static final List<Runnable> whisparr = new ArrayList<>();

    public static void main(String[] args) {
        LOGGER.info("Starting..");
        LOGGER.info("Logging mode set to {}", LogConfigVars.getLogMode(LogConfigVars.LOG_MODE).name());
        LOGGER.info("Thread pool size set to {}", Math.max(4, Runtime.getRuntime().availableProcessors() / 2));

        setupUnirest();

        for (int i = 0; i < 100; i++) {
            setupRadarr(i);
            /*
            setupSonarr(i);
            setupLidarr(i);
            setupWhisparr(i);
             */
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
                for (Runnable r : lidarr) {
                    workPool.submit(r);
                }
                for (Runnable r : whisparr) {
                    workPool.submit(r);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void setupUnirest() {
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

        RadarrV3API api = new RadarrV3API(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Radarr instance configured at {} ({})", RadarrConfigVars.URL.envName(num), url);
            return;
        }

        radarr.add(new RadarrUpdater(api));
        LOGGER.info("Added Radarr instance at {}", url);
    }

    /*
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

    private static void setupLidarr(int num) {
        String url = LidarrConfigVars.get(LidarrConfigVars.URL, num);
        String key = LidarrConfigVars.get(LidarrConfigVars.API_KEY, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Lidarr URL at {} missing", LidarrConfigVars.URL.envName(num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Lidarr API key at {} missing", LidarrConfigVars.API_KEY.envName(num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        LidarrAPI api = new LidarrAPI(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Lidarr instance configured at {} ({})", LidarrConfigVars.URL.envName(num), url);
            return;
        }

        sonarr.add(new LidarrUpdater(api));
        LOGGER.info("Added Lidarr instance at {}", url);
    }

    private static void setupWhisparr(int num) {
        String url = WhisparrConfigVars.get(WhisparrConfigVars.URL, num);
        String key = WhisparrConfigVars.get(WhisparrConfigVars.API_KEY, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Whisparr URL at {} missing", WhisparrConfigVars.URL.envName(num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Whisparr API key at {} missing", WhisparrConfigVars.API_KEY.envName(num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        WhisparrAPI api = new WhisparrAPI(url, key, num);
        if (!api.valid()) {
            LOGGER.warn("Could not authenticate to Whisparr instance configured at {} ({})", WhisparrConfigVars.URL.envName(num), url);
            return;
        }

        whisparr.add(new WhisparrUpdater(api));
        LOGGER.info("Added Whisparr instance at {}", url);
    }
     */
}
