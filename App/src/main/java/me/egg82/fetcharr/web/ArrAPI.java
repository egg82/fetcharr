package me.egg82.fetcharr.web;

import me.egg82.fetcharr.web.common.CustomFormat;
import me.egg82.fetcharr.web.common.Language;
import me.egg82.fetcharr.web.common.QualityProfile;
import me.egg82.fetcharr.web.common.Tag;
import org.jetbrains.annotations.Nullable;

public interface ArrAPI {
    boolean valid();

    int id();

    default @Nullable QualityProfile qualityProfile(int id) { return qualityProfile(id, true); }
    @Nullable QualityProfile qualityProfile(int id, boolean cache);

    default @Nullable CustomFormat customFormat(int id) { return customFormat(id, true); }
    @Nullable CustomFormat customFormat(int id, boolean cache);

    default @Nullable Language language(int id) { return language(id, true); }
    @Nullable Language language(int id, boolean cache);

    default @Nullable Tag tag(int id) { return tag(id, true); }
    @Nullable Tag tag(int id, boolean cache);
}
