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


import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.function.Function;

public final class JsonSchemaGeneratorBuilder {

    private JsonSchemaGeneratorBuilder() {
    }

    public static JsonSchemaGeneratorConfigurationBuilder draftV4Schema() {
        return new JsonSchemaGeneratorConfigurationBuilder(JsonNodeFactory.instance, config -> new JsonSchemaGenerator(config));
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

        public JsonSchemaGeneratorConfigurationBuilder disableSortSchemaProperties() {
            builder.disableSortSchemaProperties();
            return this;
        }

        public JsonSchemaGeneratorConfigurationBuilder disableProcessProperties() {
            builder.disableProcessProperties();
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
