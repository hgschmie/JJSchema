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
import static de.softwareforge.jsonschema.TestUtility.testPropertyAttribute;
import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testRequired;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

public class EmployeeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private final ObjectWriter ow = MAPPER.writerWithDefaultPrettyPrinter();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testEmployeeSchema() throws Exception {
        ObjectNode employeeSchema = generateSchema(schemaGenerator, Employee.class);

        testRequired(employeeSchema, "name");

        ObjectNode properties = testWithProperties(employeeSchema, "name", "retired");

        testPropertyType(properties, "name", "string");
        testPropertyAttribute(properties, "name","description", "Name");
        testPropertyAttribute(properties, "name","minLength", 5);
        testPropertyAttribute(properties, "name","maxLength", 50);

        testPropertyType(properties, "retired", "boolean");
    }

    static class Employee {

        private String name;
        private boolean retired;

        @JsonSchema(required = true, minLength = 5, maxLength = 50, description = "Name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty
        public boolean isRetired() {
            return retired;
        }

        public void setRetired(boolean retired) {
            this.retired = retired;
        }
    }

}
