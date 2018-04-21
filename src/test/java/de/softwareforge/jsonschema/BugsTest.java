package de.softwareforge.jsonschema;

import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.Optional;

public class BugsTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();


    // Tests whether the value in the JsonProperty is good enough to not trigger
    // reflection on the method in the NameBug bean (which does not work because it is
    // not a bean method)
    @Test
    public void testNameBug() throws Exception {
        ObjectNode schema = generateSchema(schemaGenerator, NameBug.class);
        testWithProperties(schema, "value");
    }

    public static class NameBug {

        @JsonProperty("value")
        public String value() {
            return "1";
        }
    }

    // Non-bean methods should be treated as names directly.
    @Test
    public void testPropertyBug() throws Exception {
        ObjectNode schema = generateSchema(schemaGenerator, PropertyBug.class);
        testWithProperties(schema, "xxx");
    }

    public static class PropertyBug {

        @JsonProperty
        public String xxx() {
            return "1";
        }
    }

    // Optionals should report as their member types
    @Test
    public void testOptionalBug() throws Exception {
        ObjectNode schema = generateSchema(schemaGenerator, OptionalBug.class);
        ObjectNode properties = testWithProperties(schema, "optionalString", "optionalInteger");
        assertEquals("string", properties.path("optionalString").path("type").asText());
        assertEquals("integer", properties.path("optionalInteger").path("type").asText());
    }

    public static class OptionalBug {

        @JsonProperty
        public Optional<String> optionalString() {
            return Optional.empty();
        }

        @JsonProperty
        public Optional<Integer> optionalInteger() {
            return Optional.empty();
        }
    }

}
