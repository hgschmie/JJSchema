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

package com.github.reinert.jjschema.v1;

import static com.github.reinert.jjschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.JsonSchemaGeneratorBuilder;
import com.github.reinert.jjschema.annotations.SchemaIgnore;
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
