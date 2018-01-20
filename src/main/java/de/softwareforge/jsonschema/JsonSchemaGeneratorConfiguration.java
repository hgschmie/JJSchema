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
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class JsonSchemaGeneratorConfiguration {

    public static Builder builder() {
        return new AutoValue_JsonSchemaGeneratorConfiguration.Builder()
                .setNodeFactory(JsonNodeFactory.instance)
                .setSortSchemaProperties(true)
                .setAddSchemaVersion(true)
                .setProcessProperties(true)
                .setProcessFields(false);
    }

    public abstract JsonNodeFactory nodeFactory();

    public abstract boolean addSchemaVersion();

    public abstract boolean sortSchemaProperties();

    public abstract boolean processProperties();

    public abstract boolean processFields();

    @AutoValue.Builder
    public abstract static class Builder {

        abstract Builder setNodeFactory(JsonNodeFactory nodeFactory);

        abstract Builder setAddSchemaVersion(boolean addSchemaVersion);

        abstract Builder setSortSchemaProperties(boolean sortSchemaProperties);

        abstract Builder setProcessProperties(boolean processPropertiesOnly);

        abstract Builder setProcessFields(boolean processFieldsOnly);

        public Builder removeSchemaVersion() {
            return setAddSchemaVersion(false);
        }

        public Builder disableSortSchemaProperties() {
            return setSortSchemaProperties(false);
        }

        public Builder processFields() {
            return setProcessFields(true);
        }

        public Builder disableProcessProperties() {
            return setProcessProperties(false);
        }

        public Builder customNodeFactory(JsonNodeFactory nodeFactory) {
            return setNodeFactory(nodeFactory);
        }

        public abstract JsonSchemaGeneratorConfiguration build();
    }
}
