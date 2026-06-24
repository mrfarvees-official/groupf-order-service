    package org.groupf.dto;

    import jakarta.validation.constraints.*;

    import java.time.LocalDateTime;

    public record OrderCreateRequest(
            @NotBlank(message = "Customer id is required")
            String customerId,

            @NotBlank(message = "Product id is required")
            String productId,

            @NotBlank(message = "Product name is required")
            String productName,

            @NotNull(message = "Quantity is required")
            @Min(1)
            Integer quantity,

            @NotNull(message = "Total Price is required")
            @DecimalMin(value = "0.0", inclusive = false)
            Double totalPrice,

            LocalDateTime orderDate,

            String status
    ) {
    }
