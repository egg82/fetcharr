package me.egg82.fetcharr.api.model.plugin;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.config.CommonConfigVars;
import me.egg82.fetcharr.config.PluginConfigVars;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PluginManagerImpl implements PluginManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FetcharrAPI api;

    private final List<@NotNull EnabledPluginImpl> plugins = new ArrayList<>();

    public PluginManagerImpl(@NotNull FetcharrAPI api) {
        this.api = api;

        File pluginDir = PluginConfigVars.getFile(PluginConfigVars.PLUGIN_DIR);
        File dataDir = new File(pluginDir, "data");
        File configDir = new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugin");

        downloadPlugins(new File(configDir, "plugins.yaml"), pluginDir);

        File[] files = pluginDir.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".jar"));
        if (files == null || files.length == 0) {
            logger.info("No plugins to load in {}", pluginDir.getAbsolutePath());
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        Set<@NotNull String> ids = new HashSet<>();
        for (File f : files) {
            EnabledPluginImpl plugin;
            try {
                plugin = new EnabledPluginImpl(f);
            } catch (Exception ex) {
                logger.warn("Could not load plugin at {}", f.getAbsolutePath(), ex);
                continue;
            }
            if (!ids.add(plugin.descriptor().id())) {
                logger.warn("A plugin with ID {} was already loaded. Could not load plugin at {}", plugin.descriptor().id(), f.getAbsolutePath());
                continue;
            }

            plugins.add(plugin);
            logger.info("Loaded plugin {} (\"{}\") version {} at path {}", plugin.descriptor().id(), plugin.descriptor().name(), plugin.descriptor().version(), f.getAbsolutePath());
        }

        // Init after loading all plugins so they can see each other

        List<@NotNull EnabledPluginImpl> disable = new ArrayList<>();
        for (EnabledPluginImpl p : plugins) {
            File d = new File(dataDir, p.descriptor().id());
            File c = new File(configDir, p.descriptor().id());

            if (d.exists() && !d.isDirectory()) {
                if (!d.delete()) {
                    logger.warn("Could not delete file {}", d.getAbsolutePath());
                }
            }
            if (!d.exists() && !d.mkdirs()) {
                logger.warn("Could not create directory {}", d.getAbsolutePath());
            }

            if (c.exists() && !c.isDirectory()) {
                if (!c.delete()) {
                    logger.warn("Could not delete file {}", c.getAbsolutePath());
                }
            }
            if (!c.exists() && !c.mkdirs()) {
                logger.warn("Could not create directory {}", c.getAbsolutePath());
            }

            try {
                p.plugin().init(p.context());
            } catch (Exception ex) {
                logger.warn("Could not initialize plugin {} (\"{}\")", p.descriptor().id(), p.descriptor().name(), ex);
                disable.add(p);
            }
        }
        if (!disable.isEmpty()) {
            plugins.removeAll(disable);
        }

        // We only remove plugins who failed their init, not their start

        for (EnabledPluginImpl p : plugins) {
            try {
                p.plugin().start();
            } catch (Exception ex) {
                logger.warn("Could not start plugin {} (\"{}\")", p.descriptor().id(), p.descriptor().name(), ex);
            }
        }
    }

    @Override
    public @NotNull PVector<@NotNull EnabledPlugin> plugins() {
        return TreePVector.from(plugins);
    }

    @Override
    public void shutdown() {
        for (EnabledPluginImpl p : plugins) {
            try {
                p.plugin().stop();
            } catch (Exception ex) {
                logger.warn("Plugin {} (\"{}\") encountered an error during stop()", p.descriptor().id(), p.descriptor().name());
            }
            p.close();
        }
    }

    private void downloadPlugins(@NotNull File manifest, @NotNull File pluginDir) {
        if (!manifest.exists() || !manifest.isFile()) {
            return;
        }

        if (pluginDir.exists() && !pluginDir.isDirectory()) {
            if (!pluginDir.delete()) {
                logger.warn("Could not delete file {}", pluginDir.getAbsolutePath());
                return;
            }
        }
        if (!pluginDir.exists() && !pluginDir.mkdirs()) {
            logger.warn("Could not create directory {}", pluginDir.getAbsolutePath());
            return;
        }

        logger.debug("Processing manifest {}", manifest.getAbsolutePath());

        Set<@NotNull String> urls = new HashSet<>();
        PluginManifest r = new PluginManifest();

        try (FileReader file = new FileReader(manifest); BufferedReader reader = new BufferedReader(file)) {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .source(() -> reader)
                    .build();
            readManifest(loader.load(), urls);
        } catch (IOException ex) {
            logger.error("Could not process manifest at {}", manifest.getAbsolutePath(), ex);
            return;
        }

        downloadPlugins(r, pluginDir);
    }

    private void readManifest(@NotNull CommentedConfigurationNode node, @NotNull Set<@NotNull String> urls) {

    }

    private void downloadPlugins(@NotNull PluginManifest manifest, @NotNull File pluginDir) {
        for (PluginManifestEntry e : manifest.direct()) {
            HttpResponse<byte[]> resp = Unirest.get(e.url())
                    .accept("application/java-archive")
                    .asBytes();

            if (!resp.isSuccess()) {
                logger.warn("Could not download plugin (code {}) at URL {}", resp.getStatus(), resp.getRequestSummary().getUrl());
                continue;
            }

            byte[] file = resp.getBody();
            if (file == null || file.length == 0) {
                logger.warn("Could not download plugin (no content) at URL {}", resp.getRequestSummary().getUrl());
                continue;
            }

            if (e.sha256() != null) {
                try {
                    byte[] digest = MessageDigest.getInstance("SHA-256").digest(file);
                } catch (NoSuchAlgorithmException ex) {
                    logger.warn("SHA256 for {} could not be calculated", e.url(), ex);
                }
            }

            try (FileWriter writer = new FileWriter(new File(pluginDir, e.filename() + ".tmp"))) {

            } catch (IOException ex) {
                logger.warn("Could not write temp plugin file at {}", e.filename() + ".tmp", ex);
                continue;
            }
        }
        for (PluginManifest m : manifest.manifests()) {
            downloadPlugins(m, pluginDir);
        }
    }
}
