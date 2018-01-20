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

package de.softwareforge.jsonschema.inheritance;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.JsonSchemaGenerator;
import de.softwareforge.jsonschema.JsonSchemaGeneratorBuilder;
import de.softwareforge.jsonschema.TestUtility;
import org.junit.Test;

public class InheritanceTest {
    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();

    @Test
    public void testGenerateSchema() throws JsonProcessingException {
        ObjectNode schema = TestUtility.generateSchema(schemaGenerator, MusicItem.class);
        TestUtility.testProperties(schema, "artistName", "releaseYear", "price");

        schema = TestUtility.generateSchema(schemaGenerator, WarrantyItem.class);
        TestUtility.testProperties(schema, "type", "termsAndConditionsAccepted", "contractTermInMonths");
    }

    @Test
    public void testInheritedProperties() throws JsonProcessingException {
        ObjectNode schema = TestUtility.generateSchema(schemaGenerator, CollegeStudent.class);

        ObjectNode properties = TestUtility.testWithProperties(schema, "name", "major");
        TestUtility.testPropertyAttribute(properties, "name", "description", "student name");

        TestUtility.testRequired(schema, "major", "name");
    }
}
