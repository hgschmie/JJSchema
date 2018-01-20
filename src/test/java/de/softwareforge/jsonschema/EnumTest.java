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
