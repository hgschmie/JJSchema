package com.github.reinert.jjschema.v1;

import static com.github.reinert.jjschema.TestUtility.generateSchema;
import static com.github.reinert.jjschema.TestUtility.testProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.JsonSchemaGeneratorBuilder;
import org.junit.Test;

public class JsonPropertyTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

    @Test
    public void testJsonPropertyFromMethod() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

        ObjectNode schema = generateSchema(generator, TestClass.class);
        testProperties(schema, "theOptionalProperty", "theRequiredProperty");
    }

    @Test
    public void testJsonPropertyFromField() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .disableProcessProperties()
                .processFields()
                .build();

        ObjectNode schema = generateSchema(generator, TestClass.class);
        testProperties(schema, "theOptionalField", "theRequiredField");
    }

    @Test
    public void testJsonPropertyAll() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .build();

        ObjectNode schema = generateSchema(generator, TestClass.class);
        testProperties(schema, "theOptionalField", "theRequiredField", "theOptionalProperty", "theRequiredProperty");
    }

    @Test
    public void testJsonPropertyNone() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .disableProcessProperties()
                .build();

        ObjectNode schema = generateSchema(generator, TestClass.class);
        testProperties(schema);
    }

    @Test
    public void testJsonPropertyNamed() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .build();
        ObjectNode schema = generateSchema(generator, TestNamedClass.class);
        testProperties(schema, "unnamedField", "namedField", "unnamedProperty", "namedProperty");
    }

    static class TestClass {

        private String notAField;

        @JsonProperty
        private String theOptionalField;

        @JsonProperty(required = true)
        private String theRequiredField;

        public String getNotAProperty() {
            return "no";
        }

        @JsonProperty
        public String getTheOptionalProperty() {
            return "yes";
        }

        @JsonProperty(required = true)
        public String getTheRequiredProperty() {
            return "yes, really";
        }
    }

    static class TestNamedClass {

        @JsonProperty
        private String unnamedField;

        @JsonProperty("namedField")
        private String theRequiredField;

        @JsonProperty
        public String getUnnamedProperty() {
            return "yes";
        }

        @JsonProperty(value = "namedProperty", required = true)
        public String getTheRequiredProperty() {
            return "yes, really";
        }
    }
}
