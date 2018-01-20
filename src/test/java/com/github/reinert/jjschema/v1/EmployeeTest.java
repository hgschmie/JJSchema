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
import static com.github.reinert.jjschema.TestUtility.testPropertyAttribute;
import static com.github.reinert.jjschema.TestUtility.testPropertyType;
import static com.github.reinert.jjschema.TestUtility.testRequired;
import static com.github.reinert.jjschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.JsonSchemaGenerator;
import com.github.reinert.jjschema.SchemaGeneratorBuilder;
import com.github.reinert.jjschema.annotations.JsonSchema;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class EmployeeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JsonSchemaGenerator schemaGenerator = SchemaGeneratorBuilder.draftV4Schema().build();
    private final ObjectWriter ow = MAPPER.writerWithDefaultPrettyPrinter();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testEmployeeSchema() throws Exception {
        ObjectNode employeeSchema = generateSchema(schemaGenerator, Employee.class);

        testRequired(employeeSchema, "name");

        ObjectNode properties = testWithProperties(employeeSchema, "name", "retired");

        testPropertyType(properties, "name", "string");
        testPropertyAttribute(properties, "name","description", "Name");
        testPropertyAttribute(properties, "name","minLength", 5);
        testPropertyAttribute(properties, "name","maxLength", 50);

        testPropertyType(properties, "retired", "boolean");
    }

    static class Employee {

        private String name;
        private boolean retired;

        @JsonSchema(required = true, minLength = 5, maxLength = 50, description = "Name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty
        public boolean isRetired() {
            return retired;
        }

        public void setRetired(boolean retired) {
            this.retired = retired;
        }
    }

}
