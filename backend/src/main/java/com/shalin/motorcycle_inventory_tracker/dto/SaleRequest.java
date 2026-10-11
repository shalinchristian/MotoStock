package com.shalin.motorcycle_inventory_tracker.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SaleRequest(
        @NotNull(message = "Motorcycle is required") Long motorcycleId,
        @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity,
        @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0") BigDecimal unitPrice,
        @NotBlank(message = "Customer name is required") String customerName,
        @Pattern(regexp = "^[0-9+\\- ]{0,20}$", message = "Phone number is invalid") String customerPhone,
        @Email(message = "Email is invalid") String customerEmail
) {}
