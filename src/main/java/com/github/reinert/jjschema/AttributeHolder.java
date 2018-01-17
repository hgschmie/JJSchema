package com.github.reinert.jjschema;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Collects all attributes discovered from the various annotations.
 */

@AutoValue
public abstract class AttributeHolder {

    public static Optional<AttributeHolder> locate(AnnotatedElement element) {
        checkNotNull(element, "element is null");
        Builder builder = builder();

        final Attributes attributes = element.getAnnotation(Attributes.class);
        if (attributes != null) {
            builder.attributes(attributes);
            return Optional.of(builder.build());
        }
        return Optional.empty();
    }

    public static Builder builder() {
        return new AutoValue_AttributeHolder.Builder();
    }

    public abstract Optional<String> $ref();

    public abstract Optional<String> id();

    public abstract Optional<String> title();

    public abstract Optional<String> description();

    public abstract OptionalLong maximum();

    public abstract boolean exclusiveMaximum();

    public abstract OptionalLong minimum();

    public abstract boolean exclusiveMinimum();

    public abstract Optional<String> pattern();

    public abstract Optional<String> format();

    public abstract boolean required();

    public abstract ImmutableSet<String> enums();

    public abstract OptionalLong minItems();

    public abstract OptionalLong maxItems();

    public abstract boolean uniqueItems();

    public abstract OptionalInt multipleOf();

    public abstract OptionalLong minLength();

    public abstract OptionalLong maxLength();

    public abstract boolean readonly();

    public abstract boolean additionalProperties();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder $ref(String ref);

        public abstract Builder id(String id);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder maximum(long maximum);

        public abstract Builder exclusiveMaximum(boolean exclusiveMaximum);

        public abstract Builder minimum(long minimum);

        public abstract Builder exclusiveMinimum(boolean exclusiveMinimum);

        public abstract Builder pattern(String pattern);

        public abstract Builder format(String format);

        public abstract Builder required(boolean required);

        public abstract ImmutableSet.Builder<String> enumsBuilder();

        public Builder addEnum(String enumValue) {
            enumsBuilder().add(enumValue);
            return this;
        }

        public abstract Builder minItems(long minItems);

        public abstract Builder maxItems(long maxItems);

        public abstract Builder uniqueItems(boolean uniqueItems);

        public abstract Builder multipleOf(int multipleOf);

        public abstract Builder minLength(long minLength);

        public abstract Builder maxLength(long maxLength);

        public abstract Builder readonly(boolean readOnly);

        public abstract Builder additionalProperties(boolean additionalProperties);

        public abstract AttributeHolder build();

        public Builder attributes(Attributes attributes) {
            if (!attributes.$ref().isEmpty()) {
                $ref(attributes.$ref());
            }

            if (!attributes.id().isEmpty()) {
                id(attributes.id());
            }

            if (!attributes.title().isEmpty()) {
                title(attributes.title());
            }

            if (!attributes.description().isEmpty()) {
                description(attributes.description());
            }

            if (attributes.maximum() >= 0) {
                maximum(attributes.maximum());
            }

            exclusiveMaximum(attributes.exclusiveMaximum());

            if (attributes.minimum() >= 0) {
                minimum(attributes.minimum());
            }

            exclusiveMinimum(attributes.exclusiveMinimum());

            if (!attributes.pattern().isEmpty()) {
                pattern(attributes.pattern());
            }

            if (!attributes.format().isEmpty()) {
                format(attributes.format());
            }

            required(attributes.required());

            enumsBuilder().addAll(Arrays.asList(attributes.enums()));

            if (attributes.minItems() > 0) {
                minItems(attributes.minItems());
            }

            if (attributes.maxItems() >= 0) {
                maxItems(attributes.maxItems());
            }

            uniqueItems(attributes.uniqueItems());

            if (attributes.multipleOf() > 0) {
                multipleOf(attributes.multipleOf());
            }

            if (attributes.minLength() > 0) {
                minLength(attributes.minLength());
            }

            if (attributes.maxLength() >= 0) {
                maxLength(attributes.maxLength());
            }

            readonly(attributes.readonly());
            additionalProperties(attributes.additionalProperties());

            return this;
        }
    }
}
