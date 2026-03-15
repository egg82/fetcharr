package me.egg82.arr.radarr.v3.schema;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.arr.common.AbstractAPIObject;
import me.egg82.arr.common.ArrAPI;
import me.egg82.arr.parse.BooleanParser;
import me.egg82.arr.parse.NumberParser;
import me.egg82.arr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Field extends AbstractAPIObject {
    private final int order;
    private final String name;
    private final String label;
    private final String unit;
    private final String helpText;
    private final String helpTextWarning;
    private final String helpLink;
    private final Object value;
    private final String type;
    private final boolean advanced;
    private final List<@NotNull SelectOption> selectOptions = new ArrayList<>();
    private final String selectOptionsProviderAction;
    private final String section;
    private final String hidden;
    private final PrivacyLevel privacy;
    private final String placeholder;
    private final boolean isFloat;

    public Field(@NotNull ArrAPI api, @NotNull JSONObject obj) {
        super(api, obj);

        this.order = NumberParser.getInt(-1, obj, "order");
        this.name = StringParser.get(obj, "name");
        this.label = StringParser.get(obj, "label");
        this.unit = StringParser.get(obj, "unit");
        this.helpText = StringParser.get(obj, "helpText");
        this.helpTextWarning = StringParser.get(obj, "helpTextWarning");
        this.helpLink = StringParser.get(obj, "helpLink");
        this.value = obj.has("value") ? obj.get("value") : null;
        this.type = StringParser.get(obj, "type");
        this.advanced = BooleanParser.get(false, obj, "advanced");

        JSONArray selectOptions = obj.has("selectOptions") && obj.get("selectOptions") != null ? obj.getJSONArray("selectOptions") : null;
        if (selectOptions != null) {
            for (int i = 0; i < selectOptions.length(); i++) {
                this.selectOptions.add(new SelectOption(api, selectOptions.getJSONObject(i)));
            }
        }

        this.selectOptionsProviderAction = StringParser.get(obj, "selectOptionsProviderAction");
        this.section = StringParser.get(obj, "section");
        this.hidden = StringParser.get(obj, "hidden");
        this.privacy = PrivacyLevel.get(PrivacyLevel.NORMAL, obj, "privacy");
        this.placeholder = StringParser.get(obj, "placeholder");
        this.isFloat = BooleanParser.get(false, obj, "isFloat");
    }

    public int order() {
        return order;
    }

    public @Nullable String name() {
        return name;
    }

    public @Nullable String label() {
        return label;
    }

    public @Nullable String unit() {
        return unit;
    }

    public @Nullable String helpText() {
        return helpText;
    }

    public @Nullable String helpTextWarning() {
        return helpTextWarning;
    }

    public @Nullable String helpLink() {
        return helpLink;
    }

    public @Nullable Object value() {
        return value;
    }

    public @Nullable String type() {
        return type;
    }

    public boolean advanced() {
        return advanced;
    }

    public @NotNull List<@NotNull SelectOption> selectOptions() {
        return selectOptions;
    }

    public @Nullable String selectOptionsProviderAction() {
        return selectOptionsProviderAction;
    }

    public @Nullable String section() {
        return section;
    }

    public @Nullable String hidden() {
        return hidden;
    }

    public @NotNull PrivacyLevel privacy() {
        return privacy;
    }

    public @Nullable String placeholder() {
        return placeholder;
    }

    public boolean isFloat() {
        return isFloat;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field field)) return false;
        return order == field.order && advanced == field.advanced && isFloat == field.isFloat && Objects.equals(name, field.name) && Objects.equals(label, field.label) && Objects.equals(unit, field.unit) && Objects.equals(helpText, field.helpText) && Objects.equals(helpTextWarning, field.helpTextWarning) && Objects.equals(helpLink, field.helpLink) && Objects.equals(value, field.value) && Objects.equals(type, field.type) && Objects.equals(selectOptions, field.selectOptions) && Objects.equals(selectOptionsProviderAction, field.selectOptionsProviderAction) && Objects.equals(section, field.section) && Objects.equals(hidden, field.hidden) && privacy == field.privacy && Objects.equals(placeholder, field.placeholder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, name, label, unit, helpText, helpTextWarning, helpLink, value, type, advanced, selectOptions, selectOptionsProviderAction, section, hidden, privacy, placeholder, isFloat);
    }

    @Override
    public String toString() {
        return "Field{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", unit='" + unit + '\'' +
                ", helpText='" + helpText + '\'' +
                ", helpTextWarning='" + helpTextWarning + '\'' +
                ", helpLink='" + helpLink + '\'' +
                ", value=" + value +
                ", type='" + type + '\'' +
                ", advanced=" + advanced +
                ", selectOptions=" + selectOptions +
                ", selectOptionsProviderAction='" + selectOptionsProviderAction + '\'' +
                ", section='" + section + '\'' +
                ", hidden='" + hidden + '\'' +
                ", privacy=" + privacy +
                ", placeholder='" + placeholder + '\'' +
                ", isFloat=" + isFloat +
                ", api=" + api +
                ", obj=" + obj +
                '}';
    }
}
