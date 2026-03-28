package me.egg82.fetcharr;

import kong.unirest.core.JsonNode;
import kong.unirest.core.Proxy;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.ArrType;
import me.egg82.arr.config.CacheConfigVars;
import me.egg82.arr.config.Tristate;
import me.egg82.arr.file.JSONFile;
import me.egg82.arr.lidarr.LidarrV1API;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.radarr.RadarrV3API;
import me.egg82.arr.sonarr.SonarrV3API;
import me.egg82.arr.whisparr.WhisparrV3API;
import me.egg82.fetcharr.api.APIRegistrationUtil;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.api.FetcharrAPIImpl;
import me.egg82.fetcharr.api.FetcharrAPIProvider;
import me.egg82.fetcharr.api.model.update.lidarr.LidarrUpdater;
import me.egg82.fetcharr.api.model.update.radarr.RadarrUpdater;
import me.egg82.fetcharr.api.model.update.sonarr.SonarrUpdater;
import me.egg82.fetcharr.api.model.update.whisparr.WhisparrUpdater;
import me.egg82.fetcharr.config.*;
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
import java.util.Collection;
import java.util.Map;
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

    private static volatile boolean running = true;

    public static void main(String[] args) {
        LOGGER.info("Starting..");
        LOGGER.info("Logging mode set to {}", LogConfigVars.getLogMode(LogConfigVars.LOG_MODE).name());
        LOGGER.info("Thread pool size set to {}", Math.max(4, Runtime.getRuntime().availableProcessors() / 2)); // This math is actually done in FetcharrAPIImpl

        Tristate memoryCache = CacheConfigVars.getTristate(CacheConfigVars.USE_MEMORY_CACHE);
        Tristate fileCache = CacheConfigVars.getTristate(CacheConfigVars.USE_FILE_CACHE);
        boolean cacheWritable = isCacheWritable();

        if ((fileCache == Tristate.AUTO && cacheWritable) || fileCache == Tristate.TRUE) {
            LOGGER.info("Using disk-based cache");
        } else {
            LOGGER.warn("Not using disk-based cache");
        }
        if ((memoryCache == Tristate.AUTO && fileCache == Tristate.AUTO && !cacheWritable) || (memoryCache == Tristate.AUTO && fileCache == Tristate.FALSE) || memoryCache == Tristate.TRUE) {
            LOGGER.info("Using memory-based cache");
        } else {
            LOGGER.info("Not using memory-based cache");
        }

        setupUnirest();

        LOGGER.info("---");

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        APIRegistrationUtil.register(new FetcharrAPIImpl());

        // Init after loading all plugins so they can see each other
        FetcharrAPIProvider.instance().pluginManager().init();
        FetcharrAPIProvider.instance().pluginManager().start();

        for (int i = 0; i < 100; i++) {
            setupRadarr(i);
            setupSonarr(i);
            setupLidarr(i);
            setupWhisparr(i);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down..");

            FetcharrAPI api = FetcharrAPIProvider.instance();
            api.pluginManager().shutdown();
            APIRegistrationUtil.deregister();
            api.updateManager().shutdown(10_000L);

            Unirest.shutDown();

            running = false;
        }));

        while (running) {
            try {
                Thread.sleep(5_000L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void setupUnirest() {
        String proxyHost = CommonConfigVars.get(CommonConfigVars.PROXY_HOST);
        int proxyPort = CommonConfigVars.getInt(CommonConfigVars.PROXY_PORT);
        if (proxyHost != null && proxyPort > 0) {
            Unirest.config().proxy(new Proxy(proxyHost, proxyPort));
        }

        int connectTimeout = CommonConfigVars.getInt(CommonConfigVars.CONNECT_TIMEOUT);
        if (connectTimeout > 0) {
            Unirest.config().connectTimeout(connectTimeout);
        }

        int requestTimeout = CommonConfigVars.getInt(CommonConfigVars.REQUEST_TIMEOUT);
        if (requestTimeout > 0) {
            Unirest.config().requestTimeout(requestTimeout);
        }

        int connectTTL = CommonConfigVars.getInt(CommonConfigVars.CONNECT_TTL);
        if (connectTTL > 0) {
            Unirest.config().connectionTTL(connectTTL, TimeUnit.MILLISECONDS);
        }

        boolean verifyCerts = CommonConfigVars.getBool(CommonConfigVars.VERIFY_CERTS);
        Unirest.config().disableHostNameVerification(!verifyCerts);
        Unirest.config().verifySsl(verifyCerts);

        File certsPath = CommonConfigVars.getFile(CommonConfigVars.SSL_PATH);
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
        String url = ArrConfigVars.get(ArrConfigVars.URL, ArrType.RADARR, num);
        String key = ArrConfigVars.get(ArrConfigVars.API_KEY, ArrType.RADARR, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Radarr URL at {} missing", ArrConfigVars.URL.envName(ArrType.RADARR, num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Radarr API key at {} missing", ArrConfigVars.API_KEY.envName(ArrType.RADARR, num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        RadarrV3API arrApi = new RadarrV3API(url, key, num);
        if (!arrApi.valid()) {
            LOGGER.warn("Could not authenticate to Radarr instance configured at {} ({})", ArrConfigVars.URL.envName(ArrType.RADARR, num), url);
            return;
        }

        FetcharrAPI api = FetcharrAPIProvider.instance();
        if (api.updateManager().register(new RadarrUpdater(api, arrApi, num))) {
            LOGGER.info("Added RADARR_{} instance at {}", num, url);
        } else {
            LOGGER.info("Did not add RADARR_{} instance at {} - registration cancelled", num, url);
        }
    }

    private static void setupSonarr(int num) {
        String url = ArrConfigVars.get(ArrConfigVars.URL, ArrType.SONARR, num);
        String key = ArrConfigVars.get(ArrConfigVars.API_KEY, ArrType.SONARR, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Sonarr URL at {} missing", ArrConfigVars.URL.envName(ArrType.SONARR, num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Sonarr API key at {} missing", ArrConfigVars.API_KEY.envName(ArrType.SONARR, num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        SonarrV3API arrApi = new SonarrV3API(url, key, num);
        if (!arrApi.valid()) {
            LOGGER.warn("Could not authenticate to Sonarr instance configured at {} ({})", ArrConfigVars.URL.envName(ArrType.SONARR, num), url);
            return;
        }

        FetcharrAPI api = FetcharrAPIProvider.instance();
        if (api.updateManager().register(new SonarrUpdater(api, arrApi, num))) {
            LOGGER.info("Added SONARR_{} instance at {}", num, url);
        } else {
            LOGGER.info("Did not add SONARR_{} instance at {} - registration cancelled", num, url);
        }
    }

    private static void setupLidarr(int num) {
        String url = ArrConfigVars.get(ArrConfigVars.URL, ArrType.LIDARR, num);
        String key = ArrConfigVars.get(ArrConfigVars.API_KEY, ArrType.LIDARR, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Lidarr URL at {} missing", ArrConfigVars.URL.envName(ArrType.LIDARR, num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Lidarr API key at {} missing", ArrConfigVars.API_KEY.envName(ArrType.LIDARR, num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        LidarrV1API arrApi = new LidarrV1API(url, key, num);
        if (!arrApi.valid()) {
            LOGGER.warn("Could not authenticate to Lidarr instance configured at {} ({})", ArrConfigVars.URL.envName(ArrType.LIDARR, num), url);
            return;
        }

        FetcharrAPI api = FetcharrAPIProvider.instance();
        if (api.updateManager().register(new LidarrUpdater(api, arrApi, num))) {
            LOGGER.info("Added LIDARR_{} instance at {}", num, url);
        } else {
            LOGGER.info("Did not add LIDARR_{} instance at {} - registration cancelled", num, url);
        }
    }

    private static void setupWhisparr(int num) {
        String url = ArrConfigVars.get(ArrConfigVars.URL, ArrType.WHISPARR, num);
        String key = ArrConfigVars.get(ArrConfigVars.API_KEY, ArrType.WHISPARR, num);

        if (url == null && key == null) {
            return;
        }
        if (url == null) {
            LOGGER.warn("Whisparr URL at {} missing", ArrConfigVars.URL.envName(ArrType.WHISPARR, num));
            return;
        }
        if (key == null) {
            LOGGER.warn("Whisparr API key at {} missing", ArrConfigVars.API_KEY.envName(ArrType.WHISPARR, num));
            return;
        }

        url = url.strip().replaceAll("/+$", "");
        key = key.strip();

        WhisparrV3API arrApi = new WhisparrV3API(url, key, num);
        if (!arrApi.valid()) {
            LOGGER.warn("Could not authenticate to Whisparr instance configured at {} ({})", ArrConfigVars.URL.envName(ArrType.WHISPARR, num), url);
            return;
        }

        FetcharrAPI api = FetcharrAPIProvider.instance();
        if (api.updateManager().register(new WhisparrUpdater(api, arrApi, num))) {
            LOGGER.info("Added WHISPARR_{} instance at {}", num, url);
        } else {
            LOGGER.info("Did not add WHISPARR_{} instance at {} - registration cancelled", num, url);
        }
    }

    private static boolean isCacheWritable() {
        JSONFile testFile = new JSONFile(new File(CacheConfigVars.getFile(CacheConfigVars.CACHE_DIR), "touch.json"));
        try {
            boolean writable = BooleanParser.get(false, testFile.read().getObject(), "writable");
            if (!writable) {
                testFile.write(new JsonNode(new JSONObject(Map.of("writable", true)).toString()));
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}
