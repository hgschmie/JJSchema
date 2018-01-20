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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.softwareforge.jsonschema.annotations.JsonSchema;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ProductTest {

    private final JsonSchemaGenerator schemaGenerator = JsonSchemaGeneratorBuilder.draftV4Schema().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ObjectWriter om = MAPPER.writerWithDefaultPrettyPrinter();

    /**
     * Test the scheme generate following a scheme source, avaliable at http://json-schema.org/example1.html the output should match the example.
     */
    @Test
    public void testProductSchema() throws Exception {

        final InputStream in = ProductTest.class.getResourceAsStream("/product_schema.json");
        assertNotNull("stream not found", in);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode fromFile = mapper.readTree(in);

        ObjectNode productSchema = generateSchema(schemaGenerator, Product.class);

        assertEquals(fromFile, productSchema);

        //TODO: Add support to custom Iterable classes?
        // NOTE that my implementation of ProductSet uses the ComplexProduct
        // class that inherits from Product class. That's an example of
        // inheritance support of JJSchema.
        /*
        JsonNode productSetSchema = SchemaWrapperFactory.createArrayWrapper(ProductSet.class).putDollarSchema().asJson();
        System.out.println(om.writeValueAsString(productSetSchema));
        JsonNode productSetSchemaRes = JsonLoader
                .fromResource("/products_set_schema.json");
        assertEquals(productSetSchemaRes, productSetSchema);
        */
    }

    @JsonSchema(title = "Product", description = "A product from Acme's catalog")
    static class Product {

        private long id;
        private String name;
        private BigDecimal price;
        private List<String> tags;

        @JsonSchema(required = true, description = "The unique identifier for a product")
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @JsonSchema(required = true, description = "Name of the product")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonSchema(required = true, minimum = 0, exclusiveMinimum = true)
        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        @JsonSchema(minItems = 1, uniqueItems = true)
        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    static class ComplexProduct extends Product {

        private Dimension dimensions;
        private Geo warehouseLocation;

        @JsonProperty
        public Dimension getDimensions() {
            return dimensions;
        }

        public void setDimensions(Dimension dimensions) {
            this.dimensions = dimensions;
        }

        @JsonSchema(description = "Coordinates of the warehouse with the product")
        public Geo getWarehouseLocation() {
            return warehouseLocation;
        }

        public void setWarehouseLocation(Geo warehouseLocation) {
            this.warehouseLocation = warehouseLocation;
        }

    }

    static class Dimension {

        private double length;
        private double width;
        private double height;

        @JsonSchema(required = true)
        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        @JsonSchema(required = true)
        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        @JsonSchema(required = true)
        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

    }

    @JsonSchema($ref = "http://json-schema.org/geo", description = "A geographical coordinate")
    static class Geo {

        private BigDecimal latitude;
        private BigDecimal longitude;

        @JsonProperty
        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        @JsonProperty
        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }
    }

    @JsonSchema(title = "Product set")
    static class ProductSet implements Iterable<ComplexProduct> {

        // NOTE: all custom collection types must declare the wrapped collection
        // as the first field.
        private Set<ComplexProduct> products;

        public ProductSet(Set<ComplexProduct> products) {
            this.products = products;
        }

        @Override
        public Iterator<ComplexProduct> iterator() {
            return products.iterator();
        }

    }
}
