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
package de.softwareforge.jsonschema.inheritance;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.TestUtility;
import org.junit.Test;

public class InheritanceTest {
    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

    @Test
    public void testGenerateSchema() throws JsonProcessingException {
        ObjectNode schema = TestUtility.generateSchema(schemaGenerator, MusicItem.class);
        TestUtility.testProperties(schema, "artistName", "releaseYear", "price");

        schema = TestUtility.generateSchema(schemaGenerator, WarrantyItem.class);
        TestUtility.testProperties(schema, "type", "termsAndConditionsAccepted", "contractTermInMonths");
    }

    @Test
    public void testInheritedProperties() throws JsonProcessingException {
        ObjectNode schema = TestUtility.generateSchema(schemaGenerator, CollegeStudent.class);

        ObjectNode properties = TestUtility.testWithProperties(schema, "name", "major");
        TestUtility.testPropertyAttribute(properties, "name", "description", "student name");

        TestUtility.testRequired(schema, "major", "name");
    }
}
