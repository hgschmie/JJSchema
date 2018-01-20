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

import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class GenericsTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().removeSchemaVersion().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test generating a json schema for a class that uses generic types and a collection of generic types as properties. Expect the types to be resolved
     * instead of using "object" as property types.
     */
    @Test
    public void testGenerateSchema() throws Exception {

        final InputStream in = SimpleExampleTest.class.getResourceAsStream("/generics_example.json");
        assertNotNull("stream not found", in);
        JsonNode fromResource = MAPPER.readTree(in);
        ObjectNode fromJavaType = generateSchema(schemaGenerator, GenericExample.class);

        assertEquals(fromResource, fromJavaType);
    }

    static class Tuple<A, B> {

        private A first;
        private B second;

        @JsonProperty
        public A getFirst() {
            return first;
        }

        @JsonProperty
        public B getSecond() {
            return second;
        }

        public void setFirst(A a) {
            first = a;
        }

        public void setSecond(B b) {
            second = b;
        }
    }

    static class GenericExample {

        private Tuple<String, Integer> tuple;
        private List<Tuple<String, Boolean>> listOfTuples;

        @JsonProperty
        public Tuple<String, Integer> getTuple() {
            return tuple;
        }

        public void setTuple(Tuple<String, Integer> tuple) {
            this.tuple = tuple;
        }

        @JsonProperty
        public List<Tuple<String, Boolean>> getListOfTuples() {
            return listOfTuples;
        }

        public void setListOfTuples(List<Tuple<String, Boolean>> listOfTuples) {
            this.listOfTuples = listOfTuples;
        }

    }
}
