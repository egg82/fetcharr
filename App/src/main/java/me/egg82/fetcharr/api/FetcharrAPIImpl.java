package me.egg82.fetcharr.api;

import com.sasorio.event.bus.EventBus;
import com.sasorio.event.bus.SimpleEventBus;
import com.sasorio.event.registry.EventRegistry;
import com.sasorio.event.registry.SimpleEventRegistry;
import me.egg82.fetcharr.api.event.FetcharrEvent;
import me.egg82.fetcharr.api.model.update.UpdateManager;
import me.egg82.fetcharr.api.model.update.UpdateManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;

public class FetcharrAPIImpl implements FetcharrAPI {
    private final EventRegistry<@NotNull FetcharrEvent> registry = new SimpleEventRegistry<>(FetcharrEvent.class);
    private final EventBus<@NotNull FetcharrEvent> bus;

    private UpdateManager updateManager = new UpdateManagerImpl(this, Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() / 2)));

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
    public @NotNull UpdateManager arrManager() {
        return this.updateManager;
    }

    @Override
    public void arrManager(@NotNull UpdateManager manager) {
        this.updateManager = manager;
    }
}
