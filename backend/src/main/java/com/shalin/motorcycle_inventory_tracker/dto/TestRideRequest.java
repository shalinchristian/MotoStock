package com.shalin.motorcycle_inventory_tracker.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record TestRideRequest(
        @NotNull(message = "Motorcycle is required") Long motorcycleId,
        @NotBlank(message = "Customer name is required") String customerName,
        @NotBlank(message = "Customer phone is required")
        @Pattern(regexp = "^[0-9+\\- ]{6,20}$", message = "Phone number is invalid") String customerPhone,
        @Email(message = "Email is invalid") String customerEmail,
        String licenseNumber,
        @NotNull(message = "Date and time are required") @Future(message = "Test ride must be in the future") LocalDateTime scheduledAt,
        @Min(value = 15, message = "Minimum duration is 15 minutes") @Max(value = 240, message = "Maximum duration is 240 minutes") Integer durationMinutes,
        @Size(max = 1000) String notes
) {}
