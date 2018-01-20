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

import static de.softwareforge.jsonschema.TestUtility.testProperties;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

public class FieldsOnlyTest {

    private final ObjectMapper MAPPER = new ObjectMapper();

    static class Employee {

        @JsonSchema(required = true, minLength = 5, maxLength = 50, description = "Name")
        private String name;

        @JsonProperty
        public String lastName;

        @JsonProperty
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty
        private boolean retired;

        @JsonProperty
        public boolean isRetired() {
            return retired;
        }

        public void setRetired(boolean retired) {
            this.retired = retired;
        }
    }

    @Test
    public void testFieldsOnly() throws Exception {
        JsonSchemaGenerator v4generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .disableProcessProperties()
                .build();

        ObjectNode schema = v4generator.generateSchema(Employee.class);
        testProperties(schema, "lastName", "name", "retired");
    }

    @Test
    public void testNoSortedFields() throws Exception {
        JsonSchemaGenerator v4generator = JsonSchemaGeneratorBuilder.draftV4Schema()
                .processFields()
                .disableProcessProperties()
                .disableSortSchemaProperties()
                .build();

        ObjectNode schema = v4generator.generateSchema(Employee.class);
        testProperties(schema, "name", "retired", "lastName");
    }
}
