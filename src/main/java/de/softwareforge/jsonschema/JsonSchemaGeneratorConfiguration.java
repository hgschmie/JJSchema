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
