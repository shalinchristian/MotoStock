package com.shalin.motorcycle_inventory_tracker.dto;

import jakarta.validation.constraints.NotNull;

/** Positive delta = restock, negative = write-off. */
public record StockAdjustmentRequest(@NotNull(message = "Delta is required") Integer delta, String reason) {}
