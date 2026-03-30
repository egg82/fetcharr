package me.egg82.arr.readarr.v1.model;

import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddBookOptions extends AbstractAPIObject {
    private final BookAddType addType;
    private final boolean searchForNewBook;

    public AddBookOptions(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.addType = BookAddType.get(BookAddType.MANUAL, obj, "addType");
        this.searchForNewBook = BooleanParser.get(false, obj, "searchForNewBook");
    }

    public @NotNull BookAddType addType() {
        return addType;
    }

    public boolean searchForNewBook() {
        return searchForNewBook;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddBookOptions that)) return false;
        return searchForNewBook == that.searchForNewBook && addType == that.addType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addType, searchForNewBook);
    }

    @Override
    public String toString() {
        return "AddBookOptions{" +
                "addType=" + addType +
                ", searchForNewBook=" + searchForNewBook +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
