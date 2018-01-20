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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

import java.io.InputStream;

public class SimpleExampleTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().removeSchemaVersion().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test the scheme generate following a scheme source, avaliable at http://json-schema.org/examples.html the output should match the example.
     */
    @Test
    public void testGenerateSchema() throws Exception {

        final InputStream in = SimpleExampleTest.class.getResourceAsStream("/simple_example.json");
        assertNotNull("stream not found", in);

        JsonNode fromResource = MAPPER.readTree(in);
        JsonNode fromJavaType = schemaGenerator.generateSchema(SimpleExample.class);

        assertEquals(fromResource, fromJavaType);
    }

    @JsonSchema(title = "Example Schema")
    static class SimpleExample {

        private String firstName;
        private String lastName;
        private int age;

        @JsonSchema(required = true)
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @JsonSchema(required = true)
        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @JsonSchema(description = "Age in years", minimum = 0)
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
