/*
 * Copyright (c) 2014, Danilo Reinert (daniloreinert@growbit.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.reinert.jjschema;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.function.Function;

public final class SchemaGeneratorBuilder {

    private SchemaGeneratorBuilder() {
    }

    public static JsonSchemaGeneratorConfigurationBuilder draftV4Schema() {
        return new JsonSchemaGeneratorConfigurationBuilder(JsonNodeFactory.instance, config -> new JsonSchemaGeneratorV4(config));
    }

    public static class JsonSchemaGeneratorConfigurationBuilder {

        private final Function<JsonSchemaGeneratorConfiguration, JsonSchemaGenerator> factory;
        private final JsonSchemaGeneratorConfiguration.Builder builder;

        JsonSchemaGeneratorConfigurationBuilder(JsonNodeFactory nodeFactory, Function<JsonSchemaGeneratorConfiguration, JsonSchemaGenerator> factory) {
            this.factory = factory;
            this.builder = JsonSchemaGeneratorConfiguration.builder().customNodeFactory(nodeFactory);
        }

        public JsonSchemaGeneratorConfigurationBuilder removeSchemaVersion() {
            builder.removeSchemaVersion();
            return this;
        }

        public JsonSchemaGeneratorConfigurationBuilder dontSortSchemaProperties() {
            builder.dontSortSchemaProperties();
            return this;
        }

        public JsonSchemaGeneratorConfigurationBuilder dontProcessProperties() {
            builder.dontProcessProperties();
            return this;
        }

        public JsonSchemaGeneratorConfigurationBuilder processFields() {
            builder.processFields();
            return this;
        }

        public final JsonSchemaGenerator build() {
            return factory.apply(builder.build());
        }
    }
}
