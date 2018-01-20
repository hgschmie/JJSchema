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
import static de.softwareforge.jsonschema.TestUtility.testProperties;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

import java.util.List;

public class AdditionalPropertiesTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testGenerateSaleItemSchema() throws JsonProcessingException {
        ObjectNode schema = generateSchema(schemaGenerator, SaleItem.class);
        testProperties(schema, "idSale", "name");
        assertFalse(schema.get("additionalProperties").asBoolean());
    }

    @Test
    public void testGenerateSaleSchema() throws JsonProcessingException {
        ObjectNode schema = generateSchema(schemaGenerator, Sale.class);
        assertFalse(schema.get("additionalProperties").asBoolean());
        ObjectNode properties = testWithProperties(schema, "id", "saleItems");

        // ensure additional properties is in the nested schema
        assertFalse(properties.findValue("additionalProperties").asBoolean());
    }

    @JsonSchema(title = "Sale Parent Schema", additionalProperties = false)
    static class Sale {

        int id;

        List<SaleItem> saleItems;

        @JsonProperty
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @JsonProperty
        public List<SaleItem> getSaleItems() {
            return saleItems;
        }

        public void setSaleItems(List<SaleItem> saleItems) {
            this.saleItems = saleItems;
        }
    }

    @JsonSchema(title = "Sale Item Child Schema", additionalProperties = false)
    static class SaleItem {

        int idSale;
        String name;

        @JsonProperty
        public int getIdSale() {
            return idSale;
        }

        public void setIdSale(int idSale) {
            this.idSale = idSale;
        }

        @JsonProperty
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
