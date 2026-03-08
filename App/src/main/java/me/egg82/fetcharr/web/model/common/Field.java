package me.egg82.fetcharr.web.model.common;

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.parse.BooleanParser;
import me.egg82.fetcharr.parse.NumberParser;
import me.egg82.fetcharr.parse.StringParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Field {
    private final boolean advanced;
    private final String helpLink;
    private final String helpText;
    private final String helpTextWarning;
    private final String hidden;
    private final boolean isFloat;
    private final String label;
    private final String name;
    private final int order;
    private final String placeholder;
    private final PrivacyLevel privacy;
    private final String section;
    private final Set<@NotNull SelectOption> selectOptions = new HashSet<>();
    private final String selectOptionsProviderAction;
    private final String type;
    private final String unit;
    private final String value;

    public Field(@NotNull JSONObject obj) {
        this.advanced = BooleanParser.parse(false, StringParser.parse(obj, "advanced"));
        this.helpLink = StringParser.parse(obj, "helpLink");
        this.helpText = StringParser.parse(obj, "helpText");
        this.helpTextWarning = StringParser.parse(obj, "helpTextWarning");
        this.hidden = StringParser.parse(obj, "hidden");
        this.isFloat = BooleanParser.parse(false, StringParser.parse(obj, "isFloat"));
        this.label = StringParser.parse(obj, "label");
        this.name = StringParser.parse(obj, "name");
        this.order = NumberParser.parseInt(-1, StringParser.parse(obj, "order"));
        this.placeholder = StringParser.parse(obj, "placeholder");
        this.privacy = PrivacyLevel.parse(PrivacyLevel.NORMAL, StringParser.parse(obj, "privacy"));
        this.section = StringParser.parse(obj, "section");

        JSONArray selectOptions = obj.has("selectOptions") ? obj.getJSONArray("selectOptions") : null;
        if (selectOptions != null) {
            for (int i = 0; i < selectOptions.length(); i++) {
                this.selectOptions.add(new SelectOption(selectOptions.getJSONObject(i)));
            }
        }

        this.selectOptionsProviderAction = StringParser.parse(obj, "selectOptionsProviderAction");
        this.type = StringParser.parse(obj, "type");
        this.unit = StringParser.parse(obj, "unit");
        this.value = StringParser.parse(obj, "value"); // TODO: doc says "nullable" but doesn't specify type
    }

    public boolean advanced() {
        return advanced;
    }

    public @Nullable String helpLink() {
        return helpLink;
    }

    public @Nullable String helpText() {
        return helpText;
    }

    public @Nullable String helpTextWarning() {
        return helpTextWarning;
    }

    public @Nullable String hidden() {
        return hidden;
    }

    public boolean isFloat() {
        return isFloat;
    }

    public @Nullable String label() {
        return label;
    }

    public @Nullable String name() {
        return name;
    }

    public int order() {
        return order;
    }

    public @Nullable String placeholder() {
        return placeholder;
    }

    public @NotNull PrivacyLevel privacy() {
        return privacy;
    }

    public @Nullable String section() {
        return section;
    }

    public @NotNull Set<@NotNull SelectOption> selectOptions() {
        return selectOptions;
    }

    public @Nullable String selectOptionsProviderAction() {
        return selectOptionsProviderAction;
    }

    public @Nullable String type() {
        return type;
    }

    public @Nullable String unit() {
        return unit;
    }

    public @Nullable String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field field)) return false;
        return order == field.order && Objects.equals(name, field.name) && Objects.equals(value, field.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, order, value);
    }

    @Override
    public String toString() {
        return "Field{" +
                "advanced=" + advanced +
                ", helpLink='" + helpLink + '\'' +
                ", helpText='" + helpText + '\'' +
                ", helpTextWarning='" + helpTextWarning + '\'' +
                ", hidden='" + hidden + '\'' +
                ", isFloat=" + isFloat +
                ", label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", order=" + order +
                ", placeholder='" + placeholder + '\'' +
                ", privacy=" + privacy +
                ", section='" + section + '\'' +
                ", selectOptions=" + selectOptions +
                ", selectOptionsProviderAction='" + selectOptionsProviderAction + '\'' +
                ", type='" + type + '\'' +
                ", unit='" + unit + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static class SelectOption {
        private final String hint;
        private final String name;
        private final int order;
        private final int value;

        public SelectOption(@NotNull JSONObject obj) {
            this.hint = StringParser.parse(obj, "hint");
            this.name = StringParser.parse(obj, "name");
            this.order = NumberParser.parseInt(-1, StringParser.parse(obj, "order"));
            this.value = NumberParser.parseInt(-1, StringParser.parse(obj, "value"));
        }

        public @Nullable String hint() {
            return hint;
        }

        public @Nullable String name() {
            return name;
        }

        public int order() {
            return order;
        }

        public int value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SelectOption that)) return false;
            return order == that.order && value == that.value && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, order, value);
        }

        @Override
        public String toString() {
            return "SelectOption{" +
                    "hint='" + hint + '\'' +
                    ", name='" + name + '\'' +
                    ", order=" + order +
                    ", value=" + value +
                    '}';
        }
    }
}
