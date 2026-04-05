package me.egg82.fuiplugin;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class EventLog {
    private final int maxSize;
    private final Deque<EventEntry> entries;

    public EventLog(int maxSize) {
        this.maxSize = maxSize;
        this.entries = new ArrayDeque<>(maxSize);
    }

    public synchronized void add(@NotNull EventEntry entry) {
        if (entries.size() >= maxSize) {
            entries.pollFirst();
        }
        entries.addLast(entry);
    }

    public synchronized @NotNull List<EventEntry> snapshot() {
        return new ArrayList<>(entries);
    }

    public @NotNull String toJson() {
        List<EventEntry> snapshot = snapshot();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < snapshot.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(snapshot.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }
}
