package com.shalin.motorcycle_inventory_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long id, Long motorcycleId, String motorcycleName, Integer quantity, BigDecimal unitPrice,
        BigDecimal totalPrice, String customerName, String customerPhone, String customerEmail,
        String soldBy, LocalDateTime soldAt
) {}
