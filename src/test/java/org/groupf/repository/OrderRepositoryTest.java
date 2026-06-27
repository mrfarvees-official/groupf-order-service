package org.groupf.repository;

import jakarta.persistence.EntityManager;
import org.groupf.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createSequence() {
        entityManager
                .createNativeQuery("""
                        CREATE SEQUENCE IF NOT EXISTS order_seq
                        START WITH 1
                        INCREMENT BY 1
                        """)
                .executeUpdate();
    }

    @Test
    void saveOrder_shouldGeneratePrefixedId() {
        Order order = new Order();

        order.setCustomerId("customer_1");
        order.setProductId("product_1");
        order.setProductName("Laptop");
        order.setQuantity(2);
        order.setTotalPrice(250000.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.saveAndFlush(order);

        assertNotNull(savedOrder.getOrderId());
        assertTrue(savedOrder.getOrderId().startsWith("order_"));
    }

    @Test
    void findById_shouldReturnSavedOrder() {
        Order order = new Order();

        order.setCustomerId("customer_2");
        order.setProductId("product_2");
        order.setProductName("Mouse");
        order.setQuantity(1);
        order.setTotalPrice(3500.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");

        Order savedOrder = orderRepository.saveAndFlush(order);

        Order foundOrder = orderRepository
                .findById(savedOrder.getOrderId())
                .orElseThrow();

        assertEquals(savedOrder.getOrderId(), foundOrder.getOrderId());
        assertEquals("customer_2", foundOrder.getCustomerId());
        assertEquals("product_2", foundOrder.getProductId());
        assertEquals("Mouse", foundOrder.getProductName());
        assertEquals(1, foundOrder.getQuantity());
        assertEquals(3500.0, foundOrder.getTotalPrice());
        assertEquals("COMPLETED", foundOrder.getStatus());
    }

    @Test
    void deleteOrder_shouldRemoveOrder() {
        Order order = new Order();

        order.setCustomerId("customer_3");
        order.setProductId("product_3");
        order.setProductName("Keyboard");
        order.setQuantity(1);
        order.setTotalPrice(12000.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.saveAndFlush(order);

        orderRepository.delete(savedOrder);
        orderRepository.flush();

        assertFalse(orderRepository.findById(savedOrder.getOrderId()).isPresent());
    }
}