package org.groupf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record OrderCreateRequest(
        @NotBlank (message = "Customer id is required")
        String CustomerId,

        @NotBlank (message = "Product id is required")
        String ProductId,

        @NotBlank (message = "Product name is required")
        String ProductName,

        @NotBlank(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Integer Quantity,

        @NotBlank(message = "Total Price is required")
        @Positive(message = "Total Price must be greater than 0")
        Double TotalPrice,

        LocalDateTime orderDate,

        String status
) {
}
