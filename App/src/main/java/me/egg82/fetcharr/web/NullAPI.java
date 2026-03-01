package me.egg82.fetcharr.web;

import me.egg82.fetcharr.web.common.CustomFormat;
import me.egg82.fetcharr.web.common.Language;
import me.egg82.fetcharr.web.common.QualityProfile;
import me.egg82.fetcharr.web.common.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullAPI implements ArrAPI {
    public static final NullAPI INSTANCE = new NullAPI();

    private NullAPI() { }

    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public int id() {
        return -1;
    }

    @Override
    public @NotNull String url() {
        return "";
    }

    @Override
    public @Nullable QualityProfile qualityProfile(int id, boolean cache) {
        return null;
    }

    @Override
    public @Nullable CustomFormat customFormat(int id, boolean cache) {
        return null;
    }

    @Override
    public @Nullable Language language(int id, boolean cache) {
        return null;
    }

    @Override
    public @Nullable Tag tag(int id, boolean cache) {
        return null;
    }

    @Override
    public void addTag(int itemId, int tagId) {

    }

    @Override
    public void removeTag(int itemId, int tagId) {

    }

    @Override
    public void search(int... itemIds) {

    }
}
