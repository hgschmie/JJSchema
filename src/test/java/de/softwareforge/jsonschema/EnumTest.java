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

import static de.softwareforge.jsonschema.TestUtility.testEnumValues;
import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import de.softwareforge.jsonschema.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

public class EnumTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test if @Nullable works at Collection Types
     */
    @Test
    public void testGenerateSchema() throws IOException {

        ObjectNode schema = generateSchema(schemaGenerator, Hyperthing.class);

        ObjectNode properties = testWithProperties(schema, "method", "resultCode", "floatingResultCode", "result");

        testEnumValues(properties, "method", "GET", "POST", "PUT", "DELETE");
        testEnumValues(properties, "resultCode", 404L, 401L);
        testEnumValues(properties, "floatingResultCode", new BigDecimal("4.04"), new BigDecimal("4.01"));
        testEnumValues(properties, "result", "NOT_FOUND", "UNAUTHORIZED", null);
    }

    public enum IntegerEnum {
        NOT_FOUND(404), UNAUTHORIZED(401);
        private int numVal;

        IntegerEnum(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }

        // JJSchema uses the toString method of enum to parse the accepted values
        // If the returned value is a numeric string, then it correctly parses as a number
        @Override
        public String toString() {
            return String.valueOf(numVal);
        }
    }

    public enum FloatingEnum {
        NOT_FOUND(4.04), UNAUTHORIZED(4.01);
        private double numVal;

        FloatingEnum(double numVal) {
            this.numVal = numVal;
        }

        public double getNumVal() {
            return numVal;
        }

        // JJSchema uses the toString method of enum to parse the accepted values
        // If the returned value is a numeric string, then it correctly parses as a number
        @Override
        public String toString() {
            return String.valueOf(numVal);
        }
    }

    public enum SimpleEnum {
        // For string values, there's no need to override toString
        NOT_FOUND, UNAUTHORIZED
    }

    static class Hyperthing {

        private String method;
        private IntegerEnum resultCode;
        private FloatingEnum floatingResultCode;
        private SimpleEnum result;

        @JsonSchema(enums = {"GET", "POST", "PUT", "DELETE"})
        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @JsonProperty
        public IntegerEnum getResultCode() {
            return resultCode;
        }

        public void setResultCode(IntegerEnum resultCode) {
            this.resultCode = resultCode;
        }

        @JsonProperty
        public FloatingEnum getFloatingResultCode() {
            return floatingResultCode;
        }

        public void setFloatingResultCode(FloatingEnum floatingResultCode) {
            this.floatingResultCode = floatingResultCode;
        }

        @Nullable
        public SimpleEnum getResult() {
            return result;
        }

        public void setResult(SimpleEnum result) {
            this.result = result;
        }
    }
}
