/*
 * Copyright (c) 2017, Danilo Reinert (daniloreinert@growbit.com)
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

import static com.github.reinert.jjschema.TestUtility.testPropertyType;
import static com.github.reinert.jjschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.SchemaGeneratorBuilder;
import com.github.reinert.jjschema.annotations.SchemaIgnoreProperties;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author reinert
 */
public class SchemaIgnorePropertiesTest {

    private final JsonSchemaGenerator schemaGenerator = SchemaGeneratorBuilder.draftV4Schema().build();
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
