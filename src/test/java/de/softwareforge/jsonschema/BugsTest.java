package de.softwareforge.jsonschema;

import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

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
}
