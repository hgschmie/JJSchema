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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.TestUtility;
import org.junit.Test;

public class JsonPropertyTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

    @Test
    public void testJsonPropertyFromMethod() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

        ObjectNode schema = TestUtility.generateSchema(generator, TestClass.class);
        TestUtility.testProperties(schema, "theOptionalProperty", "theRequiredProperty");
    }

    @Test
    public void testJsonPropertyFromField() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .disableProcessProperties()
                .processFields()
                .build();

        ObjectNode schema = TestUtility.generateSchema(generator, TestClass.class);
        TestUtility.testProperties(schema, "theOptionalField", "theRequiredField");
    }

    @Test
    public void testJsonPropertyAll() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .build();

        ObjectNode schema = TestUtility.generateSchema(generator, TestClass.class);
        TestUtility.testProperties(schema, "theOptionalField", "theRequiredField", "theOptionalProperty", "theRequiredProperty");
    }

    @Test
    public void testJsonPropertyNone() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .disableProcessProperties()
                .build();

        ObjectNode schema = TestUtility.generateSchema(generator, TestClass.class);
        TestUtility.testProperties(schema);
    }

    @Test
    public void testJsonPropertyNamed() throws Exception {

        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .build();
        ObjectNode schema = TestUtility.generateSchema(generator, TestNamedClass.class);
        TestUtility.testProperties(schema, "unnamedField", "namedField", "unnamedProperty", "namedProperty");
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
