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
import static de.softwareforge.jsonschema.TestUtility.testEnumValues;
import static de.softwareforge.jsonschema.TestUtility.testPropertyAttribute;
import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testRequired;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import de.softwareforge.jsonschema.annotations.Nullable;
import org.junit.Test;

import java.time.Instant;

public class InterfaceTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test the scheme generate following a scheme source, avaliable at http://json-schema.org/examples.html the output should match the example.
     */
    @Test
    public void testGenerateSchema() throws Exception {

        ObjectNode fromJavaType = generateSchema(schemaGenerator, UserInterface.class);
        ObjectNode properties = testWithProperties(fromJavaType, "birthday", "id", "name", "photo", "sex");

        testPropertyType(properties,"id", "integer");
        testPropertyAttribute(properties, "id", "title", "ID");
        testPropertyAttribute(properties, "id", "minimum", 100000);
        testPropertyAttribute(properties, "id", "maximum", 999999);

        testPropertyType(properties,"name", "string");
        testPropertyAttribute(properties, "name", "description", "User's name");

        testPropertyType(properties,"sex", "string", "null");
        testEnumValues(properties, "sex", "M", "F", null);

        testPropertyType(properties,"photo", "object", "null");
        testPropertyAttribute(properties, "photo", "description", "User's personal photo");

        testPropertyType(properties,"birthday", "string");
        testPropertyAttribute(properties, "birthday", "format", "date-time");

        testRequired(fromJavaType, "id", "name");
    }

    interface UserInterface {

        @JsonSchema(required = true, title = "ID", minimum = 100000, maximum = 999999)
        short getId();

        void setId(short id);

        @JsonSchema(required = true, description = "User's name")
        String getName();

        void setName(String name);

        @JsonSchema(description = "User's sex", enums = {"M", "F"})
        @Nullable
        char getSex();

        void setSex(char sex);

        @JsonSchema(description = "User's personal photo")
        @Nullable
        Byte[] getPhoto();

        void setPhoto(Byte[] photo);

        @JsonSchema(format = "date-time")
        Instant getBirthday();
    }
}
