package org.groupf.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.groupf.dto.OrderCreateRequest;
import org.groupf.entity.Order;
import org.groupf.dto.OrderCreatedEvent;
import org.groupf.publisher.OrderEventPublisher;
import org.groupf.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @PostMapping
    public Order createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order order = new Order();

        order.setCustomerId(request.CustomerId());
        order.setProductId(request.ProductId());
        order.setProductName(request.ProductName());
        order.setQuantity(request.Quantity());
        order.setOrderDate(request.orderDate());
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
}