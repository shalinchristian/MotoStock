package com.shalin.motorcycle_inventory_tracker.dto;

import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull(message = "Status is required") BookingStatus status) {}
