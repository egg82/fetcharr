package me.egg82.fetcharr.api.model.plugin;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.RawResponse;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.api.FetcharrAPI;
import me.egg82.fetcharr.config.CommonConfigVars;
import me.egg82.fetcharr.config.PluginConfigVars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class PluginManagerImpl implements PluginManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FetcharrAPI api;

    private final List<@NotNull EnabledPluginImpl> plugins = new ArrayList<>();

    private static final PluginClassLoaderPolicy LOADER_POLICY = new PluginClassLoaderPolicy(List.of(
            "java.",
            "javax.",
            "jdk.",
            "sun.",
            "org.slf4j.",
            "org.jetbrains.annotations.",
            "org.spongepowered.configurate.",
            "me.egg82.fetcharr.api."
    ));

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    public PluginManagerImpl(@NotNull FetcharrAPI api) {
        this.api = api;

        File pluginDir = PluginConfigVars.getFile(PluginConfigVars.PLUGIN_DIR);

        downloadPlugins(new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugins.yaml"), pluginDir);
        downloadPlugins(new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugins.yml"), pluginDir);
        downloadPlugins(new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugin.yaml"), pluginDir);
        downloadPlugins(new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugin.yml"), pluginDir);

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
                plugin = new EnabledPluginImpl(f, LOADER_POLICY, this::resolveClass);
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
    }

    @Override
    public void init() {
        if (!initialized.compareAndSet(false, true)) {
            logger.warn("Attempted to re-initialize plugins.");
            return;
        }

        File pluginDir = PluginConfigVars.getFile(PluginConfigVars.PLUGIN_DIR);
        File dataDir = new File(pluginDir, "data");
        File pluginConfigDir = new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugin");

        List<@NotNull EnabledPluginImpl> disable = new ArrayList<>();
        for (EnabledPluginImpl p : plugins) {
            File d = new File(dataDir, p.descriptor().id());
            File c = new File(pluginConfigDir, p.descriptor().id());

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
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            logger.warn("Attempted to re-start plugins.");
            return;
        }

        // We only remove plugins who failed their init, not ones that failed their start

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

    private @Nullable EnabledPluginImpl resolveClass(@NotNull EnabledPluginImpl requester, @NotNull String className) {
        for (EnabledPluginImpl p : plugins) {
            if (p == requester) { // == works in this instance
                continue;
            }
            Set<@NotNull String> exports = p.descriptor().exports();
            if (exports == null || exports.isEmpty()) {
                continue;
            }
            for (String e : exports) {
                // Is exact match? If not, is in package (or sub-package) of exported?
                if (className.equals(e) || className.startsWith(e + ".")) {
                    logger.debug("Using {} as provider for {}", p.descriptor().id(), className);
                    return p;
                }
            }
        }
        return null;
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
            readManifest(r, manifest.getAbsolutePath(), loader.load(), urls);
        } catch (IOException ex) {
            logger.error("Could not process manifest at {}", manifest.getAbsolutePath(), ex);
            return;
        }

        downloadPlugins(r, pluginDir);
    }

    private void readManifest(@NotNull PluginManifest manifest, @NotNull String url, @NotNull CommentedConfigurationNode node, @NotNull Set<@NotNull String> urls) {
        if (!node.hasChild("plugins")) {
            logger.debug("Manifest at {} does not have plugins list. Skipping.", url);
            return;
        }
        List<CommentedConfigurationNode> inner = node.node("plugins").childrenList();
        for (CommentedConfigurationNode i : inner) {
            String m = i.node("manifest").getString();
            if (m != null && !m.isBlank()) {
                if (!urls.add(m)) {
                    logger.debug("Manifest at {} attempted to add duplicate manifest URL {}", url, m);
                    continue;
                }

                HttpResponse<String> resp = Unirest.get(m)
                        .accept("application/yaml")
                        .asString();

                if (!resp.isSuccess()) {
                    logger.warn("Could not get manifest (code {}) at URL {}", resp.getStatus(), resp.getRequestSummary().getUrl());
                    continue;
                }

                String body = resp.getBody();
                if (body == null || body.isBlank()) {
                    logger.warn("Could not get manifest (no content) at URL {}", resp.getRequestSummary().getUrl());
                    continue;
                }

                try (StringReader str = new StringReader(body); BufferedReader reader = new BufferedReader(str)) {
                    YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                            .source(() -> reader)
                            .build();
                    PluginManifest mm = new PluginManifest();
                    readManifest(mm, m, loader.load(), urls);
                    manifest.manifests().add(mm);
                } catch (IOException ex) {
                    logger.warn("Could not process manifest at URL {}", resp.getRequestSummary().getUrl(), ex);
                    continue;
                }
            }

            String u = i.node("url").getString();
            String f = i.node("filename").getString();
            String s = i.node("sha256").getString();

            if (u == null || u.isBlank()) {
                logger.warn("Manifest {} has a missing plugin URL", url);
                continue;
            }

            if (f == null || f.isBlank()) {
                logger.warn("Manifest {} has a missing filename", url);
                continue;
            }

            if (!urls.add(u)) {
                logger.debug("Manifest at {} attempted to add duplicate plugin URL {}", url, u);
                continue;
            }

            manifest.direct().add(new PluginManifestEntry(u, f, s));
        }
    }

    // ChatGPT wrote the memory-optimized version of this downloading/unpacking. The original was a little too memory-heavy
    // Small edits were done to this method
    private void downloadPlugins(@NotNull PluginManifest manifest, @NotNull File pluginDir) {
        for (PluginManifestEntry e : manifest.direct()) {
            File target = new File(pluginDir, e.filename());
            File temp = new File(pluginDir, e.filename() + ".tmp");

            try {
                MessageDigest digest = (e.sha256() != null) ? MessageDigest.getInstance("SHA-256") : null;

                try (InputStream raw = Unirest.get(e.url()).accept("application/java-archive").asObject(RawResponse::getContent).getBody(); InputStream in = new BufferedInputStream(raw); OutputStream out = new BufferedOutputStream(new FileOutputStream(temp))) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        if (digest != null) {
                            digest.update(buffer, 0, read);
                        }
                    }
                }

                if (digest != null) {
                    String actual = toHex(digest.digest());
                    if (!actual.equalsIgnoreCase(e.sha256())) {
                        logger.warn("SHA256 of downloaded file ({}) does not match given SHA256 ({}) for {}",
                                actual, e.sha256(), e.url());
                        if (!temp.delete()) {
                            logger.warn("Could not delete temp file {}", temp.getAbsolutePath());
                        }
                        continue;
                    }
                }

                java.nio.file.Files.move(
                        temp.toPath(),
                        target.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE
                );
            } catch (Exception ex) {
                logger.warn("Could not download plugin at URL {}", e.url(), ex);
                if (temp.exists() && !temp.delete()) {
                    logger.warn("Could not delete temp file {}", temp.getAbsolutePath());
                }
            }
        }

        for (PluginManifest m : manifest.manifests()) {
            downloadPlugins(m, pluginDir);
        }
    }

    private static @NotNull String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = HEX[v >>> 4];
            out[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(out);
    }
}
