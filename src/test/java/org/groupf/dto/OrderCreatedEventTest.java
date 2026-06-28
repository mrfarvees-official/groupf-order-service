package org.groupf.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreatedEventTest {

    @Test
    void shouldCreateOrderCreatedEvent() {
        LocalDateTime time = LocalDateTime.now();

        OrderCreatedEvent event = new OrderCreatedEvent(
                "order_1",
                "customer_1",
                "product_1",
                "Laptop",
                2,
                time,
                "PENDING"
        );

        assertEquals("order_1", event.orderId());
        assertEquals("customer_1", event.customerId());
        assertEquals("product_1", event.productId());
        assertEquals("Laptop", event.productName());
        assertEquals(2, event.quantity());
        assertEquals(time, event.orderDate());
        assertEquals("PENDING", event.status());
    }

    @Test
    void records_shouldBeEqualWhenSameData() {
        LocalDateTime time = LocalDateTime.now();

        OrderCreatedEvent e1 = new OrderCreatedEvent(
                "order_1", "customer_1", "product_1", "Laptop", 2, time, "PENDING"
        );

        OrderCreatedEvent e2 = new OrderCreatedEvent(
                "order_1", "customer_1", "product_1", "Laptop", 2, time, "PENDING"
        );

        assertEquals(e1, e2);
    }
}