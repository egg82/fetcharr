package me.egg82.fetcharr.api.model.plugin;

import me.egg82.fetcharr.api.plugin.*;
import me.egg82.fetcharr.config.CommonConfigVars;
import me.egg82.fetcharr.config.PluginConfigVars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EnabledPluginImpl implements EnabledPlugin {
    private final Plugin plugin;
    private final PluginDescriptor descriptor;
    private final PluginContext context;
    private final PluginClassLoader classLoader;

    private static final String @NotNull [] YAML_PATHS = new String[] {
            "plugin.yaml",
            "plugin.yml",
            "META-INF/plugin.yaml",
            "META-INF/plugin.yml",
    };

    public EnabledPluginImpl(@NotNull File jarFile, @NotNull PluginClassLoaderPolicy loaderPolicy, @NotNull PluginClassLoaderResolver resolver) {
        if (!jarFile.exists() || !jarFile.isFile()) {
            throw new IllegalArgumentException("Plugin file " + jarFile.getAbsolutePath() + " does not exist or is not a file");
        }

        PluginClassLoader classLoader = null;
        try {
            this.descriptor = readDescriptor(jarFile);
            classLoader = new PluginClassLoader(this, new URL[] { jarFile.toURI().toURL() }, Plugin.class.getClassLoader(), loaderPolicy, resolver);
            this.context = createContext(jarFile, descriptor);
            this.plugin = createPlugin(classLoader, descriptor);
            this.classLoader = classLoader;
        } catch (PluginLoadException ex) {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException ignored) { }
            }
            throw ex;
        } catch (Exception ex) {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException ignored) { }
            }
            throw new PluginLoadException(jarFile, "Plugin at " + jarFile.getAbsolutePath() + " could not be loaded", ex);
        }
    }

    @Override
    public @NotNull Plugin plugin() {
        return this.plugin;
    }

    @Override
    public @NotNull PluginDescriptor descriptor() {
        return this.descriptor;
    }

    @Override
    public @NotNull PluginContext context() {
        return this.context;
    }

    public @NotNull PluginClassLoader classLoader() {
        return classLoader;
    }

    public void close() {
        try {
            classLoader.close();
        } catch (IOException ignored) { }
    }

    private @NotNull PluginDescriptor readDescriptor(@NotNull File jarFile) throws IOException, IllegalStateException {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = null;
            for (String path : YAML_PATHS) {
                entry = jar.getJarEntry(path);
                if (entry != null) {
                    break;
                }
            }
            if (entry == null) {
                throw new IOException("missing plugin.yaml for plugin at " + jarFile.getAbsolutePath());
            }

            try (InputStreamReader input = new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(input)) {
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .source(() -> reader)
                        .build();
                CommentedConfigurationNode node = loader.load();
                String id = requiredString(jarFile, node, "id");
                if (!id.matches("[a-z0-9._-]+")) {
                    throw new IllegalStateException("id " + id + " is invalid in plugin.yaml for plugin at " + jarFile.getAbsolutePath());
                }
                return new PluginDescriptorImpl(id, requiredString(jarFile, node, "name"), node.node("description").getString(), optionalStringSet(node, "authors"), requiredString(jarFile, node, "version"), requiredString(jarFile, node, "class"), optionalStringSet(node, "exports"));
            }
        }
    }

    private @NotNull String requiredString(@NotNull File jarFile, @NotNull CommentedConfigurationNode node, @NotNull String key) throws IllegalStateException {
        String r = node.node(key).getString();
        if (r == null) {
            throw new IllegalStateException("missing " + key + " from plugin.yaml for plugin at " + jarFile.getAbsolutePath());
        }
        return r;
    }

    private @Nullable Set<@NotNull String> optionalStringSet(@NotNull CommentedConfigurationNode node, @NotNull String key) {
        CommentedConfigurationNode n = node.node(key);
        if (n.empty() || !n.isList()) {
            return null;
        }
        try {
            List<@NotNull String> l = n.getList(String.class);
            return l != null ? Set.copyOf(l) : null;
        } catch (SerializationException ignored) {
            return null;
        }
    }

    private @NotNull PluginContext createContext(@NotNull File jarFile, @NotNull PluginDescriptor descriptor) {
        File dataDir = new File(PluginConfigVars.getFile(PluginConfigVars.PLUGIN_DIR), "data");
        File configDir = new File(CommonConfigVars.getFile(CommonConfigVars.CONFIG_DIR), "plugin");
        return new PluginContextImpl(new File(dataDir, descriptor.id()), new File(configDir, descriptor.id()), jarFile);
    }

    private @NotNull Plugin createPlugin(@NotNull URLClassLoader classLoader, @NotNull PluginDescriptor descriptor) throws ReflectiveOperationException {
        Class<?> raw = Class.forName(descriptor.className(), true, classLoader);
        Class<? extends Plugin> plugin = raw.asSubclass(Plugin.class);
        return plugin.getDeclaredConstructor().newInstance();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EnabledPluginImpl that)) return false;
        return Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptor);
    }

    @Override
    public String toString() {
        return "EnabledPluginImpl{" +
                "plugin=" + plugin +
                ", descriptor=" + descriptor +
                ", context=" + context +
                ", classLoader=" + classLoader +
                '}';
    }
}
