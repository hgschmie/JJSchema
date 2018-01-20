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

import static com.github.reinert.jjschema.TestUtility.generateSchema;
import static com.github.reinert.jjschema.TestUtility.testPropertyType;
import static com.github.reinert.jjschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.JsonSchemaGeneratorBuilder;
import com.github.reinert.jjschema.annotations.Nullable;
import org.junit.Test;

import java.util.List;

public class NullableArrayTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test if @Nullable works at Collection Types
     */
    @Test
    public void testGenerateSchema() {

        ObjectNode schema = generateSchema(schemaGenerator, Something.class);

        ObjectNode properties = testWithProperties(schema, "id", "names");

        testPropertyType(properties, "names", "array", "null");
        }

    static class Something {

        private int id;
        private List<String> names;

        @JsonProperty
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Nullable
        @JsonProperty
        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

    }
}
