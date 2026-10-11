package com.shalin.motorcycle_inventory_tracker.dto;

import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;

import java.time.LocalDateTime;

public record TestRideResponse(
        Long id, Long motorcycleId, String motorcycleName, String customerName, String customerPhone,
        String customerEmail, String licenseNumber, LocalDateTime scheduledAt, Integer durationMinutes,
        BookingStatus status, String notes
) {}
