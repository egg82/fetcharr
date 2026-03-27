package me.egg82.fwebhook.internal.api;

import me.egg82.fwebhook.api.WebhookAPI;
import me.egg82.fwebhook.api.webhook.WebhookDestination;
import org.jetbrains.annotations.NotNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebhookAPIImpl implements WebhookAPI {
    private final ConcurrentMap<String, @NotNull WebhookDestination> destinations = new ConcurrentHashMap<>();

    public WebhookAPIImpl() { }

    @Override
    public boolean register(@NotNull WebhookDestination destination) {
        return destinations.putIfAbsent(destination.id(), destination) == null;
    }

    @Override
    public boolean unregister(@NotNull WebhookDestination destination) {
        return destinations.remove(destination.id()) != null;
    }

    @Override
    public @NotNull PVector<@NotNull WebhookDestination> destinations() {
        return TreePVector.from(destinations.values());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebhookAPIImpl that)) return false;
        return Objects.equals(destinations, that.destinations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(destinations);
    }

    @Override
    public String toString() {
        return "WebhookAPIImpl{" +
                "destinations=" + destinations +
                '}';
    }
}
