package org.groupf.dto;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
        String orderId,
        String customerId,
        String productId,
        String productName,
        Integer quantity,
        LocalDateTime orderDate,
        String status
) {
}