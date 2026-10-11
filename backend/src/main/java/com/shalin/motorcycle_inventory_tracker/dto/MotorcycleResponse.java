package com.shalin.motorcycle_inventory_tracker.dto;

import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;

import java.math.BigDecimal;

public record MotorcycleResponse(
        Long id, String brand, String model, Integer manufacturingYear, BigDecimal price, String color,
        Integer engineCapacityCc, BigDecimal motorPowerKw, Integer stockQuantity, Integer lowStockThreshold, boolean lowStock,
        BikeCategory category, String photoUrl, Integer powerHp, Integer torqueNm, Integer weightKg,
        Integer topSpeedKmh, String fuelType, String transmission, String description
) {}
