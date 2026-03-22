package me.egg82.fetcharr.api.model.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class PluginClassLoader extends URLClassLoader {
    // ChatGPT wrote about 40% of this class

    static {
        registerAsParallelCapable();
    }

    private final EnabledPluginImpl owner;
    private final PluginClassLoaderPolicy loaderPolicy;
    private final PluginClassLoaderResolver resolver;

    public PluginClassLoader(@NotNull EnabledPluginImpl owner, @NotNull URL[] urls, @NotNull ClassLoader parent, @NotNull PluginClassLoaderPolicy loaderPolicy, @NotNull PluginClassLoaderResolver resolver) {
        super(urls, parent);
        this.owner = owner;
        this.loaderPolicy = loaderPolicy;
        this.resolver = resolver;
    }

    public @NotNull Class<?> loadExportedClass(@NotNull String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) {
                return clazz;
            }
            return findClass(name);
        }
    }

    @Override
    public @Nullable URL getResource(@NotNull String name) {
        URL local = findResource(name);
        return local != null ? local : getParent().getResource(name);
    }

    @Override
    public @NotNull Enumeration<URL> getResources(@NotNull String name) throws IOException {
        Set<URL> urls = new LinkedHashSet<>();

        Enumeration<URL> local = findResources(name);
        while (local.hasMoreElements()) {
            urls.add(local.nextElement());
        }

        Enumeration<URL> parent = getParent().getResources(name);
        while (parent.hasMoreElements()) {
            urls.add(parent.nextElement());
        }

        return Collections.enumeration(urls);
    }

    @Override
    protected Class<?> loadClass(@NotNull String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            if (loaderPolicy.isParentFirst(name)) {
                clazz = getParent().loadClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            try {
                clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException ignored) { }

            EnabledPluginImpl exportedOwner = resolver.resolve(owner, name);
            if (exportedOwner != null) {
                clazz = exportedOwner.classLoader().loadExportedClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            clazz = getParent().loadClass(name);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }
}
