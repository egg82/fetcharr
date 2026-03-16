package me.egg82.arr.common;

import org.jetbrains.annotations.NotNull;

public class NullArrAPI extends AbstractArrAPI {
    public static NullArrAPI INSTANCE = new NullArrAPI();

    private NullArrAPI() {
        super("", "", -1);
    }

    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public @NotNull ArrType type() {
        return ArrType.UNKNOWN;
    }

    @Override
    public @NotNull String version() {
        return "";
    }

    @Override
    public void search(int... itemIds) {

    }
}
