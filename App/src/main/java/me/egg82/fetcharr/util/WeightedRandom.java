package me.egg82.fetcharr.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class WeightedRandom<T extends Weighted> {
    private List<T> objs;
    private final Random r = new SecureRandom(); // Doesn't really need to be SecureRandom, but I mean, why not use it?

    public WeightedRandom() {
        objs = List.of();
    }

    public WeightedRandom(Collection<T> arr) {
        objs = new ArrayList<>(arr);
    }

    public WeightedRandom(T[] arr) {
        this(Arrays.asList(arr));
    }

    public void updateList(T[] arr) {
        updateList(Arrays.asList(arr));
    }

    public void updateList(Collection<T> arr) {
        objs = new ArrayList<>(arr);
    }

    public void clear() {
        objs.clear();
    }

    public @Nullable T selectOne() {
        if (objs.isEmpty()) {
            return null;
        }

        Instant now = Instant.now();

        long totalWeight = 0L;
        long[] weights = new long[objs.size()];

        for (int i = 0; i < objs.size(); i++) {
            long weight = calculate(objs.get(i), now);
            weights[i] = weight;
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            return null;
        }

        long l = r.nextLong(totalWeight);
        long running = 0L;
        for (int i = 0; i < weights.length; i++) {
            running += weights[i];
            if (l < running) {
                T o = objs.get(i);
                o.lastSelectedNow();
                return o;
            }
        }

        return null;
    }

    // Math is hard, so ChatGPT'd this bit
    private long calculate(@NotNull T obj, @NotNull Instant now) {
        double hoursSinceUpdated = Math.max(0.0d, Duration.between(obj.lastUpdated(), now).toMinutes() / 60.0d);
        double hoursSinceSelected = Math.max(0.0d, Duration.between(obj.lastSelected(), now).toMinutes() / 60.0d);

        // How quickly stale items gain priority (~63% of max by this point)
        double updatedTauHours = 24.0d * 7.0d; // 1 week

        // Maximum extra boost from staleness
        double maxUpdatedBoost = 4.0d; // total updatedFactor approaches 5.0

        // How quickly a selected item becomes eligible again
        double selectedTauHours = 24.0d * 3.0d; // 3 days

        // Immediately after selection, keep it possible but unlikely
        double minSelectedRecovery = 0.05d;

        // Starts at 1.0, rises toward 1.0 + maxUpdatedBoost
        double updatedFactor = 1.0d + maxUpdatedBoost * (1.0d - Math.exp(-hoursSinceUpdated / updatedTauHours));

        // Starts at minSelectedRecovery, rises toward 1.0
        double selectedFactor = minSelectedRecovery + (1.0d - minSelectedRecovery) * (1.0d - Math.exp(-hoursSinceSelected / selectedTauHours));

        double weight = updatedFactor * selectedFactor;

        // Scale to preserve precision before converting to long
        return Math.max(1L, Math.round(weight * 1000.0d));
    }
}
