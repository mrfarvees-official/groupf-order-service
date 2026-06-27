package org.groupf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.groupf.dto.OrderCreateRequest;
import org.groupf.dto.OrderCreatedEvent;
import org.groupf.dto.ProductResponse;
import org.groupf.entity.Order;
import org.groupf.publisher.OrderEventPublisher;
import org.groupf.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@TestPropertySource(properties = {
        "product.service.url=http://localhost:8081"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderEventPublisher orderEventPublisher;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void createOrder_shouldSavePublishEventAndReturnOrder() throws Exception {
        LocalDateTime orderDate = LocalDateTime.of(2026, 6, 27, 10, 30);

        OrderCreateRequest request = new OrderCreateRequest(
                "customer_0001",
                "product_0001",
                "Laptop",
                2,
                1.0,
                orderDate,
                "PENDING"
        );

        ProductResponse productResponse = new ProductResponse(
                "product_0001",
                "Laptop",
                250000.0,
                "Gaming laptop",
                "Electronics",
                5
        );

        Order savedOrder = new Order(
                "order_0001",
                "customer_0001",
                "product_0001",
                "Laptop",
                2,
                500000.0,
                orderDate,
                "PENDING"
        );

        when(restTemplate.getForObject(
                "http://localhost:8081/products/product_0001",
                ProductResponse.class
        )).thenReturn(productResponse);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order_0001"))
                .andExpect(jsonPath("$.customerId").value("customer_0001"))
                .andExpect(jsonPath("$.productId").value("product_0001"))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(500000.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(restTemplate, times(1)).getForObject(
                "http://localhost:8081/products/product_0001",
                ProductResponse.class
        );

        verify(orderRepository, times(1)).save(any(Order.class));

        verify(orderEventPublisher, times(1))
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_whenProductNotFound_shouldThrowException() {
        LocalDateTime orderDate = LocalDateTime.of(2026, 6, 27, 10, 30);

        OrderCreateRequest request = new OrderCreateRequest(
                "customer_0001",
                "product_9999",
                "Unknown Product",
                1,
                100.0,
                orderDate,
                "PENDING"
        );

        when(restTemplate.getForObject(
                "http://localhost:8081/products/product_9999",
                ProductResponse.class
        )).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(exception.getMessage().contains("Product not found with id: product_9999"));

        verify(restTemplate, times(1)).getForObject(
                "http://localhost:8081/products/product_9999",
                ProductResponse.class
        );

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never())
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_whenStockIsInsufficient_shouldThrowException() {
        LocalDateTime orderDate = LocalDateTime.of(2026, 6, 27, 10, 30);

        OrderCreateRequest request = new OrderCreateRequest(
                "customer_0001",
                "product_0001",
                "Laptop",
                10,
                1.0,
                orderDate,
                "PENDING"
        );

        ProductResponse productResponse = new ProductResponse(
                "product_0001",
                "Laptop",
                250000.0,
                "Gaming laptop",
                "Electronics",
                5
        );

        when(restTemplate.getForObject(
                "http://localhost:8081/products/product_0001",
                ProductResponse.class
        )).thenReturn(productResponse);

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(exception.getMessage().contains("Insufficient stock for product id: product_0001"));

        verify(restTemplate, times(1)).getForObject(
                "http://localhost:8081/products/product_0001",
                ProductResponse.class
        );

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never())
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        LocalDateTime orderDate = LocalDateTime.of(2026, 6, 27, 10, 30);

        OrderCreateRequest request = new OrderCreateRequest(
                "",
                "product_0001",
                "Laptop",
                2,
                1.0,
                orderDate,
                "PENDING"
        );

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(restTemplate, never())
                .getForObject(anyString(), eq(ProductResponse.class));

        verify(orderRepository, never()).save(any(Order.class));

        verify(orderEventPublisher, never())
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
    }
}