package org.groupf.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void shouldCreateProductResponse() {
        ProductResponse product = new ProductResponse(
                "product_1",
                "Laptop",
                250000.0,
                "Gaming laptop",
                "Electronics",
                10
        );

        assertEquals("product_1", product.productId());
        assertEquals("Laptop", product.name());
        assertEquals(250000.0, product.unitPrice());
        assertEquals("Gaming laptop", product.description());
        assertEquals("Electronics", product.category());
        assertEquals(10, product.stock());
    }

    @Test
    void records_shouldBeEqualWhenValuesSame() {
        ProductResponse p1 = new ProductResponse(
                "product_1", "Laptop", 250000.0, "Gaming laptop", "Electronics", 10
        );

        ProductResponse p2 = new ProductResponse(
                "product_1", "Laptop", 250000.0, "Gaming laptop", "Electronics", 10
        );

        assertEquals(p1, p2);
    }
}