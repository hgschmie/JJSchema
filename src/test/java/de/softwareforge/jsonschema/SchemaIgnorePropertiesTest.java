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

import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.SchemaIgnoreProperties;
import org.junit.Test;

public class SchemaIgnorePropertiesTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test if @SchemaIgnore works correctly
     */
    @Test
    public void testGenerateSchema() throws JsonProcessingException {

        ObjectNode schema = schemaGenerator.generateSchema(ItemWrapper.class);

        ObjectNode properties = testWithProperties(schema, "id", "item");
        testPropertyType(properties, "item", "object");

        ObjectNode itemNode = (ObjectNode) properties.get("item");

        assertFalse(itemNode.has("properties"));
    }

    static class ItemWrapper {

        int id;

        Item item;

        @JsonProperty
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @SchemaIgnoreProperties
        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }
    }

    static class Item {

        int itemId;
        String itemName;


        @JsonProperty
        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        @JsonProperty
        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }
}
