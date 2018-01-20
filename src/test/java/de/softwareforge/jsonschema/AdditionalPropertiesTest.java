/*
 * Copyright (c) 2014, Danilo Reinert (daniloreinert@growbit.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
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
