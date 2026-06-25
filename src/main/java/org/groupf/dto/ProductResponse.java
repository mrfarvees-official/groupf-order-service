package org.groupf.dto;

public record ProductResponse(
        String productId,
        String name,
        Double unitPrice,
        String description,
        String category,
        Integer stock
) {
}