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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.JsonSchemaGeneratorBuilder;
import com.github.reinert.jjschema.annotations.JsonSchema;
import org.junit.Test;

import java.io.InputStream;

public class SimpleTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().removeSchemaVersion().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test the scheme generate following a scheme source, avaliable at http://json-schema.org/examples.html the output should match the example.
     */
    @Test
    public void testGenerateSchema() throws Exception {

        final InputStream in = SimpleTest.class.getResourceAsStream("/simple_example.json");
        assertNotNull("stream not found", in);

        JsonNode fromResource = MAPPER.readTree(in);
        JsonNode fromJavaType = schemaGenerator.generateSchema(SimpleExample.class);

        assertEquals(fromResource, fromJavaType);
    }

    @JsonSchema(title = "Example Schema")
    static class SimpleExample {

        private String firstName;
        private String lastName;
        private int age;

        @JsonSchema(required = true)
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @JsonSchema(required = true)
        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @JsonSchema(description = "Age in years", minimum = 0)
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
