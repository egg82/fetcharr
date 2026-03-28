package me.egg82.fetcharr.api.model.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MissingStatus {
    ALL(new String[] { "all", "everything" }),
    MISSING(new String[] { "missing", "missingonly", "missing-only", "missing_only" }),
    UPGRADE(new String[] { "upgrade", "nonmissing", "non-missing", "non_missing", "notmissing", "not-missing", "not_missing" });

    private final String[] names;
    MissingStatus(@NotNull String @NotNull [] names) {
        this.names = names;
    }

    public @NotNull String @NotNull [] names() {
        return names;
    }

    public static @NotNull MissingStatus parse(@NotNull MissingStatus def, @Nullable String val) {
        MissingStatus r = parse(val);
        return r != null ? r : def;
    }

    public static @Nullable MissingStatus parse(@Nullable String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        val = val.trim();

        for (MissingStatus m : MissingStatus.values()) {
            for (String v : m.names) {
                if (v.equalsIgnoreCase(val)) {
                    return m;
                }
            }
        }
        return null;
    }
}
