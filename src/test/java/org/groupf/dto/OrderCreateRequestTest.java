package org.groupf.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private OrderCreateRequest createValidRequest() {
        return new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                2,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );
    }

    @Test
    void validRequest_shouldHaveNoValidationErrors() {
        OrderCreateRequest request = createValidRequest();

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankCustomerId_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "",
                "product_1",
                "Laptop",
                2,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Customer id is required")));
    }

    @Test
    void blankProductId_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "",
                "Laptop",
                2,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Product id is required")));
    }

    @Test
    void blankProductName_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "",
                2,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Product name is required")));
    }

    @Test
    void nullQuantity_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                null,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void quantity_lessThanOne_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                0,
                250000.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void nullTotalPrice_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                2,
                null,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void zeroTotalPrice_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                2,
                0.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void negativeTotalPrice_shouldFailValidation() {
        OrderCreateRequest request = new OrderCreateRequest(
                "customer_1",
                "product_1",
                "Laptop",
                2,
                -10.0,
                LocalDateTime.now(),
                "PENDING"
        );

        Set<ConstraintViolation<OrderCreateRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}