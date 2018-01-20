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
import static de.softwareforge.jsonschema.TestUtility.testPropertyAttribute;
import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testRequired;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

public class EmployeeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
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
