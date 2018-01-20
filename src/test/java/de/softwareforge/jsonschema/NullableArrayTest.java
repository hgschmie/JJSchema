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

import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.Nullable;
import org.junit.Test;

import java.util.List;

public class NullableArrayTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test if @Nullable works at Collection Types
     */
    @Test
    public void testGenerateSchema() {

        ObjectNode schema = generateSchema(schemaGenerator, Something.class);

        ObjectNode properties = testWithProperties(schema, "id", "names");

        testPropertyType(properties, "names", "array", "null");
        }

    static class Something {

        private int id;
        private List<String> names;

        @JsonProperty
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Nullable
        @JsonProperty
        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

    }
}
