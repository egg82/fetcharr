package me.egg82.fetcharr.web;

import me.egg82.fetcharr.web.common.CustomFormat;
import me.egg82.fetcharr.web.common.Language;
import me.egg82.fetcharr.web.common.QualityProfile;
import me.egg82.fetcharr.web.common.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ArrAPI {
    boolean valid();

    int id();
    @NotNull String url();

    default @Nullable QualityProfile qualityProfile(int id) { return qualityProfile(id, true); }
    @Nullable QualityProfile qualityProfile(int id, boolean cache);

    default @Nullable CustomFormat customFormat(int id) { return customFormat(id, true); }
    @Nullable CustomFormat customFormat(int id, boolean cache);

    default @Nullable Language language(int id) { return language(id, true); }
    @Nullable Language language(int id, boolean cache);

    default @Nullable Tag tag(int id) { return tag(id, true); }
    @Nullable Tag tag(int id, boolean cache);

    void addTag(int itemId, int tagId);
    void removeTag(int itemId, int tagId);

    default void search(int itemId) { search(new int[] { itemId }); }
    default void search(Collection<Integer> ids) {
        int[] a = new int[ids.size()];
        int i = 0;
        for (int x : ids) {
            a[i] = x;
            i++;
        }
        search(a);
    }
    void search(int... itemIds);
}
