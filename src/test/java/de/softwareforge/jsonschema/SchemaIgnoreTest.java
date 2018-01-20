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

import static de.softwareforge.jsonschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.SchemaIgnore;
import org.junit.Test;

import java.util.List;

public class SchemaIgnoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

    /**
     * Test if @SchemaIgnore works correctly
     */
    @Test
    public void testGenerateSaleSchema() {

        ObjectNode schema = schemaGenerator.generateSchema(Sale.class);
        testWithProperties(schema, "id");
    }

    public void testGenerateSaleItemSchema() {

        ObjectNode schema = schemaGenerator.generateSchema(SaleItem.class);
        testWithProperties(schema, "idSale", "seqNumber");
    }

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

        @SchemaIgnore
        @JsonProperty
        public List<SaleItem> getSaleItems() {
            return saleItems;
        }

        public void setSaleItems(List<SaleItem> saleItems) {
            this.saleItems = saleItems;
        }
    }

    static class SaleItem {

        int idSale;
        int seqNumber;
        Sale parent;

        @JsonProperty
        public int getIdSale() {
            return idSale;
        }

        public void setIdSale(int idSale) {
            this.idSale = idSale;
        }

        @JsonProperty
        public int getSeqNumber() {
            return seqNumber;
        }

        public void setSeqNumber(int seqNumber) {
            this.seqNumber = seqNumber;
        }

        @SchemaIgnore
        @JsonProperty
        public Sale getParent() {
            return parent;
        }

        public void setParent(Sale parent) {
            this.parent = parent;
        }
    }
}
