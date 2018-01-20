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

package de.softwareforge.jsonschema;

import static de.softwareforge.jsonschema.TestUtility.generateSchema;
import static de.softwareforge.jsonschema.TestUtility.testEnumValues;
import static de.softwareforge.jsonschema.TestUtility.testPropertyAttribute;
import static de.softwareforge.jsonschema.TestUtility.testPropertyType;
import static de.softwareforge.jsonschema.TestUtility.testRequired;
import static de.softwareforge.jsonschema.TestUtility.testWithProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import de.softwareforge.jsonschema.annotations.Nullable;
import org.junit.Test;

import java.time.Instant;

public class InterfaceTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Test the scheme generate following a scheme source, avaliable at http://json-schema.org/examples.html the output should match the example.
     */
    @Test
    public void testGenerateSchema() throws Exception {

        ObjectNode fromJavaType = generateSchema(schemaGenerator, UserInterface.class);
        ObjectNode properties = testWithProperties(fromJavaType, "birthday", "id", "name", "photo", "sex");

        testPropertyType(properties,"id", "integer");
        testPropertyAttribute(properties, "id", "title", "ID");
        testPropertyAttribute(properties, "id", "minimum", 100000);
        testPropertyAttribute(properties, "id", "maximum", 999999);

        testPropertyType(properties,"name", "string");
        testPropertyAttribute(properties, "name", "description", "User's name");

        testPropertyType(properties,"sex", "string", "null");
        testEnumValues(properties, "sex", "M", "F", null);

        testPropertyType(properties,"photo", "object", "null");
        testPropertyAttribute(properties, "photo", "description", "User's personal photo");

        testPropertyType(properties,"birthday", "string");
        testPropertyAttribute(properties, "birthday", "format", "date-time");

        testRequired(fromJavaType, "id", "name");
    }

    interface UserInterface {

        @JsonSchema(required = true, title = "ID", minimum = 100000, maximum = 999999)
        short getId();

        void setId(short id);

        @JsonSchema(required = true, description = "User's name")
        String getName();

        void setName(String name);

        @JsonSchema(description = "User's sex", enums = {"M", "F"})
        @Nullable
        char getSex();

        void setSex(char sex);

        @JsonSchema(description = "User's personal photo")
        @Nullable
        Byte[] getPhoto();

        void setPhoto(Byte[] photo);

        @JsonSchema(format = "date-time")
        Instant getBirthday();
    }
}
