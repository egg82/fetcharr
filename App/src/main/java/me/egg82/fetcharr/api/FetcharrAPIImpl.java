package me.egg82.fetcharr.api;

import com.sasorio.event.bus.EventBus;
import com.sasorio.event.bus.SimpleEventBus;
import com.sasorio.event.registry.EventRegistry;
import com.sasorio.event.registry.SimpleEventRegistry;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.model.plugin.PluginManager;
import me.egg82.fetcharr.api.model.plugin.PluginManagerImpl;
import me.egg82.fetcharr.api.model.update.UpdateManager;
import me.egg82.fetcharr.api.model.update.UpdateManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public class FetcharrAPIImpl implements FetcharrAPI {
    private final EventRegistry<@NotNull FetcharrEvent> registry = new SimpleEventRegistry<>(FetcharrEvent.class);
    private final EventBus<@NotNull FetcharrEvent> bus;

    private UpdateManager updateManager = new UpdateManagerImpl(this, Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2)));
    private final PluginManager pluginManager = new PluginManagerImpl(this);

    public FetcharrAPIImpl() {
        this.bus = new SimpleEventBus<>(registry, new EventExceptionHandlerImpl());
    }

    @Override
    public @NotNull EventRegistry<@NotNull FetcharrEvent> registry() {
        return this.registry;
    }

    @Override
    public @NotNull EventBus<@NotNull FetcharrEvent> bus() {
        return this.bus;
    }

    @Override
    public @NotNull UpdateManager updateManager() {
        return this.updateManager;
    }

    @Override
    public void updateManager(@NotNull UpdateManager manager) {
        this.updateManager = manager;
    }

    @Override
    public @NotNull PluginManager pluginManager() {
        return this.pluginManager;
    }

    @Override
    public String toString() {
        return "FetcharrAPIImpl{" +
                "registry=" + registry +
                ", bus=" + bus +
                ", updateManager=" + updateManager +
                ", pluginManager=" + pluginManager +
                '}';
    }
}
