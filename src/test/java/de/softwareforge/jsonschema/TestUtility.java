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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Ignore
public final class TestUtility {

    private TestUtility() {
        throw new AssertionError();
    }

    public static ObjectNode generateSchema(JsonSchemaGenerator generator, Class<?> testClass) {
        return generator.generateSchema(testClass);
    }

    public static Optional<ObjectNode> testProperties(ObjectNode schema, String... expectedFields) {
        if (expectedFields.length == 0) {
            assertFalse("unexpected properties attribute found", schema.has("properties"));
            return Optional.empty();
        } else {
            assertTrue("no properties attribute found", schema.has("properties"));

            JsonNode child = schema.get("properties");
            assertTrue("properties is not an object", child.isObject());
            ObjectNode properties = (ObjectNode) child;

            assertEquals("number of properties does not match", expectedFields.length, properties.size());
            for (String field : expectedFields) {
                assertTrue("No field '" + field + "' found!", properties.has(field));
            }
            return Optional.of(properties);
        }
    }

    public static ObjectNode testWithProperties(ObjectNode schema, String firstField, String... expectedFields) {
        String[] fields = new String[expectedFields.length + 1];
        fields[0] = firstField;
        System.arraycopy(expectedFields, 0, fields, 1, expectedFields.length);
        Optional<ObjectNode> schemaHolder = testProperties(schema, fields);
        assertTrue("no properties found", schemaHolder.isPresent());
        return schemaHolder.get();
    }

    public static void testRequired(ObjectNode schema, String... expectedFields) {
        if (expectedFields.length == 0) {
            assertFalse("unexpected required attribute found", schema.has("required"));
        } else {
            assertTrue("no required attribute found", schema.has("required"));
            JsonNode child = schema.get("required");
            assertTrue("required is not an array", child.isArray());

            ArrayNode required = (ArrayNode) child;
            assertEquals("number of elements in required field does not match", expectedFields.length, required.size());

            String[] values = new String[required.size()];
            for (int i = 0; i < expectedFields.length; i++) {
                values[i] = required.get(i).asText();
            }

            assertThat("required values do not match", Arrays.asList(expectedFields), containsInAnyOrder(values));
        }
    }

    public static void testEnumValues(ObjectNode schema, String property, Object... expectedValues) {
        assertTrue("property '" + property + "' not present", schema.has(property));
        JsonNode child = schema.get(property);
        assertTrue("'" + property + "' is not an object", child.isObject());
        ObjectNode propertyNode = (ObjectNode) child;
        assertTrue("'" + property + "' is not an enum", propertyNode.has("enum"));
        child = propertyNode.get("enum");
        assertTrue("'enum' is not an array", child.isArray());
        ArrayNode enumNode = (ArrayNode) child;

        assertEquals("number of elements in enum does not match", expectedValues.length, enumNode.size());

        Object[] values = new Object[enumNode.size()];
        for (int i = 0; i < expectedValues.length; i++) {
            JsonNode node = enumNode.get(i);
            if (node.isNumber()) {
                values[i] = node.numberValue();
            } else if (node.isBoolean()) {
                values[i] = node.booleanValue();
            } else if (node.isNull()) {
                values[i] = null;
            } else {
                values[i] = node.asText();
            }
        }

        assertThat("enum values do not match", Arrays.asList(expectedValues), containsInAnyOrder(values));
    }

    public static void testPropertyAttribute(ObjectNode schema, String property, String attribute, Object... values) {
        assertTrue("property '" + property + "' not present", schema.has(property));
        JsonNode child = schema.get(property);
        assertTrue("'" + property + "' is not an object", child.isObject());
        ObjectNode propertyNode = (ObjectNode) child;

        if (values.length == 0) {
            if (!propertyNode.has(attribute)) {
                return;
            }
            JsonNode attributeNode = propertyNode.get(attribute);
            if (attributeNode.isArray() || ((ArrayNode) attributeNode).size() == 0) {
                return;
            }
            fail("property attribute '" + property + "." + attribute + "' present.");
        } else {
            assertTrue("attribute '" + property + "." + attribute + "' not present", propertyNode.has(attribute));
            JsonNode attributeNode = propertyNode.get(attribute);

            if (values.length == 1) {
                if (attributeNode.isArray()) {
                    if ((attributeNode).size() != 1) {
                        fail("property attribute '" + property + "." + attribute + "' must have a single value");
                    }
                    attributeNode = attributeNode.get(0);
                }

                Object value = values[0];
                Object attributeValue = fetchAttributeValue(attributeNode);
                assertEquals(value, attributeValue);
            } else {
                assertTrue("attribute '" + property + "." + attribute + "' not an array", attributeNode.isArray());
                Object[] attributeValues = new Object[values.length];

                for (int i = 0; i < values.length; i++) {
                    attributeValues[i] = fetchAttributeValue(attributeNode.get(i));
                }

                assertArrayEquals(values, attributeValues);
            }
        }
    }

    private static Object fetchAttributeValue(JsonNode valueNode) {
        if (valueNode.isNull()) {
            return null;
        } else if (valueNode.isBoolean()) {
            return valueNode.booleanValue();
        } else if (valueNode.isNumber()) {
            return valueNode.numberValue();
        } else if (valueNode.isTextual()) {
            return valueNode.textValue();
        } else if (valueNode.isArray()) {
            return Streams.stream(valueNode.iterator()).collect(toList());
        } else if (valueNode.isObject()) {
            return Streams.stream(valueNode.fields()).collect(toMap(Map.Entry::getKey, (value) -> fetchAttributeValue(value.getValue())));
        }
        fail("Unknown node type: " + valueNode);
        return null;
    }

    public static void testPropertyType(ObjectNode schema, String property, Object... values) {
        testPropertyAttribute(schema, property, "type", values);
    }
}
