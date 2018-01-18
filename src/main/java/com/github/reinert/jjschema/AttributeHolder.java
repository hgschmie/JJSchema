package com.github.reinert.jjschema;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Collects all attributes discovered from the various annotations.
 */

@AutoValue
public abstract class AttributeHolder {

    public static Optional<AttributeHolder> locate(AnnotatedElement element) {
        checkNotNull(element, "element is null");
        Builder builder = builder();

        boolean foundAnnotations = false;
        final JsonSchema jsonSchema = element.getAnnotation(JsonSchema.class);
        if (jsonSchema != null) {
            builder.attributes(jsonSchema);
            foundAnnotations = true;
        }

        final JsonProperty jsonProperty = element.getAnnotation(JsonProperty.class);
        if (jsonProperty != null && jsonProperty.required()) {
            builder.required(true);
            foundAnnotations = true;
        }

        return foundAnnotations ? Optional.of(builder.build()) : Optional.empty();
    }

    public static Builder builder() {
        return new AutoValue_AttributeHolder.Builder()
                .exclusiveMaximum(false)
                .exclusiveMinimum(false)
                .required(false)
                .uniqueItems(false)
                .readonly(false)
                .additionalProperties(true);
    }

    public abstract Optional<String> $ref();

    public abstract Optional<String> id();

    public abstract Optional<String> title();

    public abstract Optional<String> description();

    public abstract OptionalInt maximum();

    public abstract boolean exclusiveMaximum();

    public abstract OptionalInt minimum();

    public abstract boolean exclusiveMinimum();

    public abstract Optional<String> pattern();

    public abstract Optional<String> format();

    public abstract boolean required();

    public abstract ImmutableSet<String> enums();

    public abstract OptionalInt minItems();

    public abstract OptionalInt maxItems();

    public abstract boolean uniqueItems();

    public abstract OptionalInt multipleOf();

    public abstract OptionalInt minLength();

    public abstract OptionalInt maxLength();

    public abstract boolean readonly();

    public abstract boolean additionalProperties();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder $ref(String ref);

        public abstract Builder id(String id);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder maximum(int maximum);

        public abstract Builder exclusiveMaximum(boolean exclusiveMaximum);

        public abstract Builder minimum(int minimum);

        public abstract Builder exclusiveMinimum(boolean exclusiveMinimum);

        public abstract Builder pattern(String pattern);

        public abstract Builder format(String format);

        public abstract Builder required(boolean required);

        public abstract ImmutableSet.Builder<String> enumsBuilder();

        public Builder addEnum(String enumValue) {
            enumsBuilder().add(enumValue);
            return this;
        }

        public abstract Builder minItems(int minItems);

        public abstract Builder maxItems(int maxItems);

        public abstract Builder uniqueItems(boolean uniqueItems);

        public abstract Builder multipleOf(int multipleOf);

        public abstract Builder minLength(int minLength);

        public abstract Builder maxLength(int maxLength);

        public abstract Builder readonly(boolean readOnly);

        public abstract Builder additionalProperties(boolean additionalProperties);

        public abstract AttributeHolder build();

        public Builder attributes(JsonSchema jsonSchema) {
            if (!jsonSchema.$ref().isEmpty()) {
                $ref(jsonSchema.$ref());
            }

            if (!jsonSchema.id().isEmpty()) {
                id(jsonSchema.id());
            }

            if (!jsonSchema.title().isEmpty()) {
                title(jsonSchema.title());
            }

            if (!jsonSchema.description().isEmpty()) {
                description(jsonSchema.description());
            }

            if (jsonSchema.maximum() >= 0) {
                maximum(jsonSchema.maximum());
            }

            exclusiveMaximum(jsonSchema.exclusiveMaximum());

            if (jsonSchema.minimum() >= 0) {
                minimum(jsonSchema.minimum());
            }

            exclusiveMinimum(jsonSchema.exclusiveMinimum());

            if (!jsonSchema.pattern().isEmpty()) {
                pattern(jsonSchema.pattern());
            }

            if (!jsonSchema.format().isEmpty()) {
                format(jsonSchema.format());
            }

            required(jsonSchema.required());

            enumsBuilder().addAll(Arrays.asList(jsonSchema.enums()));

            if (jsonSchema.minItems() > 0) {
                minItems(jsonSchema.minItems());
            }

            if (jsonSchema.maxItems() >= 0) {
                maxItems(jsonSchema.maxItems());
            }

            uniqueItems(jsonSchema.uniqueItems());

            if (jsonSchema.multipleOf() > 0) {
                multipleOf(jsonSchema.multipleOf());
            }

            if (jsonSchema.minLength() > 0) {
                minLength(jsonSchema.minLength());
            }

            if (jsonSchema.maxLength() >= 0) {
                maxLength(jsonSchema.maxLength());
            }

            readonly(jsonSchema.readonly());
            additionalProperties(jsonSchema.additionalProperties());

            return this;
        }
    }
}
