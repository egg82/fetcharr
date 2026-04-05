package me.egg82.fuiplugin;

import com.sasorio.event.EventConfig;
import me.egg82.fetcharr.api.FetcharrAPIProvider;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.plugin.Plugin;
import me.egg82.fetcharr.api.plugin.PluginContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;

public class UIPlugin implements Plugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean started = false;
    private UIServer server;
    private EventLog eventLog;

    public UIPlugin() {
        logger.info("UI plugin ready to init");
    }

    @Override
    public void init(@NotNull PluginContext context) throws Exception {
        CommentedConfigurationNode config = loadConfig(new File(context.configDir(), "config.yaml"));
        int port = config != null ? config.node("port").getInt(8080) : 8080;
        int maxEvents = config != null ? config.node("max-events").getInt(500) : 500;

        eventLog = new EventLog(maxEvents);
        server = new UIServer(port, eventLog);

        FetcharrAPIProvider.instance().events().subscribe(
                FetcharrEvent.class,
                EventConfig.of(Integer.MAX_VALUE, false, false),
                this::onEvent
        );

        logger.info("UI plugin initialized, will serve on port {}", port);
    }

    @Override
    public void start() throws Exception {
        server.start();
        started = true;
        logger.info("UI plugin started");
    }

    @Override
    public void stop() throws Exception {
        started = false;
        if (server != null) {
            server.stop();
        }
        logger.info("UI plugin stopped");
    }

    private void onEvent(@NotNull FetcharrEvent event) {
        if (!started) return;
        eventLog.add(EventEntry.from(event));
        server.broadcast(eventLog.toJson());
    }

    private @Nullable CommentedConfigurationNode loadConfig(@NotNull File configFile) {
        if (!configFile.exists() || !configFile.isFile() || configFile.length() == 0L) {
            try (InputStream resource = getClass().getResourceAsStream("/config.yaml");
                 FileWriter out = new FileWriter(configFile);
                 BufferedWriter writer = new BufferedWriter(out)) {

                if (resource == null) {
                    logger.error("Could not get resource /config.yaml");
                    return null;
                }

                File parent = configFile.getParentFile();
                if (parent.exists() && !parent.isDirectory()) {
                    if (!parent.delete()) {
                        logger.error("Could not delete file {}", parent.getAbsolutePath());
                        return null;
                    }
                }
                if (!parent.exists() && !parent.mkdirs()) {
                    logger.error("Could not create directory {}", parent.getAbsolutePath());
                    return null;
                }

                byte[] buffer = new byte[250];
                int len;
                while ((len = resource.read(buffer)) > 0) {
                    char[] chars = new char[len];
                    for (int i = 0; i < len; i++) {
                        chars[i] = (char) buffer[i];
                    }
                    writer.write(chars);
                }
            } catch (IOException ex) {
                logger.error("Could not write default config to {}", configFile.getAbsolutePath(), ex);
                return null;
            }
        }

        try (FileReader file = new FileReader(configFile);
             BufferedReader reader = new BufferedReader(file)) {
            return YamlConfigurationLoader.builder()
                    .source(() -> reader)
                    .build()
                    .load();
        } catch (IOException ex) {
            logger.error("Could not read config file at {}", configFile.getAbsolutePath(), ex);
            return null;
        }
    }
}
