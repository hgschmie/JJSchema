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
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

public class FormatTest {

    private ObjectNode properties;

    @Before
    public void setUp() throws Exception {
        JsonSchemaGenerator generator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

        ObjectNode schema = generateSchema(generator, TestClass.class);
        properties = testWithProperties(schema, "hinted", "formatted", "overridden", "vanilla");
    }

    @Test
    public void testFormattingHinted() {
        testPropertyType(properties, "hinted", "string");
        testPropertyAttribute(properties, "hinted", "format", "date-time");
    }

    @Test
    public void testFormattingFormatted() {
        testPropertyType(properties, "formatted", "string");
        testPropertyAttribute(properties, "formatted", "format", "weird");
    }

    @Test
    public void testFormattingOverridden() {
        testPropertyType(properties, "overridden", "string");
        testPropertyAttribute(properties, "overridden", "format", "very-weird");
    }

    @Test
    public void testFormattingVanilla() {
        testPropertyType(properties, "vanilla", "string");
        testPropertyAttribute(properties, "vanilla", "format");
    }

    public interface TestClass {

        @JsonProperty
        public Instant getHinted();

        @JsonSchema(format = "weird")
        public String getFormatted();

        @JsonSchema(format = "very-weird")
        public Instant getOverridden();

        @JsonProperty
        public String getVanilla();
    }
}
