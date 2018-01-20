/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.softwareforge.jsonschema;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import de.softwareforge.jsonschema.annotations.Nullable;
import de.softwareforge.jsonschema.annotations.SchemaIgnore;
import de.softwareforge.jsonschema.annotations.SchemaIgnoreProperties;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

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
        if (jsonProperty != null) {
            foundAnnotations = true;
            if (jsonProperty.required()) {
                builder.required(true);
            }
            if (!jsonProperty.value().isEmpty()) {
                builder.named(jsonProperty.value());
            }
        }

        final SchemaIgnore schemaIgnore = element.getAnnotation(SchemaIgnore.class);
        if (schemaIgnore != null) {
            foundAnnotations = true;
            builder.ignored(true);
        }

        final SchemaIgnoreProperties schemaIgnoreProperties = element.getAnnotation(SchemaIgnoreProperties.class);
        if (schemaIgnoreProperties != null) {
            foundAnnotations = true;
            builder.ignoredProperties(true);
        }

        final Nullable nullable = element.getAnnotation(Nullable.class);
        if (nullable != null) {
            foundAnnotations = true;
            builder.nullable(true);
        }

        final JsonManagedReference jsonManagedReference = element.getAnnotation(JsonManagedReference.class);
        if (jsonManagedReference != null) {
            foundAnnotations = true;
            builder.managedReference(jsonManagedReference.value());
        }

        final JsonBackReference jsonBackReference = element.getAnnotation(JsonBackReference.class);
        if (jsonBackReference != null) {
            foundAnnotations = true;
            builder.backReference(jsonBackReference.value());
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
                .additionalProperties(true)
                .ignored(false)
                .ignoredProperties(false)
                .nullable(false);
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

    public abstract boolean ignored();

    public abstract boolean ignoredProperties();

    public abstract boolean nullable();

    public abstract Optional<String> named();

    public abstract Optional<String> managedReference();

    public abstract Optional<String> backReference();

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

        public abstract Builder ignored(boolean ignored);

        public abstract Builder ignoredProperties(boolean ignored);

        public abstract Builder nullable(boolean nullable);

        public abstract Builder named(String name);

        public abstract Builder managedReference(String managedReference);

        public abstract Builder backReference(String backReference);

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

    void augmentCommonAttributes(ObjectNode node) {
        id().ifPresent(id -> node.put("id", id));
        description().ifPresent(description -> node.put("description", description));
        pattern().ifPresent(pattern -> node.put("pattern", pattern));
        format().ifPresent(format -> node.put("format", format));
        title().ifPresent(title -> node.put("title", title));
        maximum().ifPresent(maximum -> node.put("maximum", maximum));

        if (exclusiveMaximum()) {
            node.put("exclusiveMaximum", true);
        }

        minimum().ifPresent(minimum -> node.put("minimum", minimum));

        if (exclusiveMinimum()) {
            node.put("exclusiveMinimum", true);
        }

        if (!enums().isEmpty()) {
            ArrayNode enumArray = node.putArray("enum");
            for (String v : enums()) {
                enumArray.add(v);
            }
        }

        if (uniqueItems()) {
            node.put("uniqueItems", true);
        }

        minItems().ifPresent(minItems -> node.put("minItems", minItems));
        maxItems().ifPresent(maxItems -> node.put("maxItems", maxItems));
        multipleOf().ifPresent(multipleOf -> node.put("multipleOf", multipleOf));
        minLength().ifPresent(minLength -> node.put("minLength", minLength));
        maxLength().ifPresent(maxLength -> node.put("maxLength", maxLength));

        if (readonly()) {
            node.put("readonly", true);
        }
    }
}
