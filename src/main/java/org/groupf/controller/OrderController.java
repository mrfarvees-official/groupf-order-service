package org.groupf.controller;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.groupf.dto.OrderCreateRequest;
import org.groupf.dto.ProductResponse;
import org.groupf.entity.Order;
import org.groupf.dto.OrderCreatedEvent;
import org.groupf.publisher.OrderEventPublisher;
import org.groupf.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @PostMapping
    public Order createOrder(@Valid @RequestBody OrderCreateRequest request) {

        ProductResponse product = restTemplate.getForObject(
                productServiceUrl + "/products/" + request.productId(),
                ProductResponse.class
        );

        if (product == null) {
            throw new RuntimeException("Product not found with id: " + request.productId());
        }

        if (product.stock() < request.quantity()) {
            throw new RuntimeException("Insufficient stock for product id: " + request.productId());
        }

        double totalPrice = product.unitPrice() * request.quantity();

        Order order = new Order();

        order.setCustomerId(request.customerId());
        order.setProductId(request.productId());
        order.setProductName(request.productName());
        order.setQuantity(request.quantity());
        order.setOrderDate(request.orderDate());
        order.setTotalPrice(totalPrice);
        order.setStatus(request.status());

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getOrderId(),
                savedOrder.getCustomerId(),
                savedOrder.getProductId(),
                savedOrder.getProductName(),
                savedOrder.getQuantity(),
                savedOrder.getOrderDate(),
                savedOrder.getStatus()
        );

        orderEventPublisher.publishOrderCreatedEvent(event);

        return savedOrder;
    }

    @GetMapping
    public Page<Order> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @PutMapping("/{orderId}")
    public Order updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        ProductResponse product = restTemplate.getForObject(
                productServiceUrl + "/products/" + existingOrder.getProductId(),
                ProductResponse.class
        );

        if (product == null) {
            throw new RuntimeException("Product not found with id: " + existingOrder.getProductId());
        }

        if (product.stock() < request.quantity()) {
            throw new RuntimeException("Insufficient stock for product id: " + existingOrder.getProductId());
        }

        double totalPrice = product.unitPrice() * request.quantity();

        existingOrder.setProductName(product.name());
        existingOrder.setQuantity(request.quantity());
        existingOrder.setTotalPrice(totalPrice);
        existingOrder.setOrderDate(request.orderDate());
        existingOrder.setStatus(request.status());

        return orderRepository.save(existingOrder);
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable String orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        orderRepository.delete(existingOrder);

        return "Order deleted successfully with id: " + orderId;
    }

}