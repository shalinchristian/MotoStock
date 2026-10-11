package com.shalin.motorcycle_inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** A bike model held in stock by the dealership. Validation lives on the request DTO. */
@Entity
@Table(name = "motorcycles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Motorcycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer manufacturingYear;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String color;

    /** Petrol/hybrid bikes only; null for ELECTRIC. */
    private Integer engineCapacityCc;

    /** Electric motor rated power in kW; ELECTRIC bikes only. */
    @Column(precision = 6, scale = 1)
    private BigDecimal motorPowerKw;

    @Column(nullable = false)
    private Integer stockQuantity;

    /** Stock at or below this level raises a low-stock alert. */
    private Integer lowStockThreshold = 2;

    @Enumerated(EnumType.STRING)
    private BikeCategory category = BikeCategory.STANDARD;

    private String photoUrl;

    // Specs
    private Integer powerHp;
    private Integer torqueNm;
    private Integer weightKg;
    private Integer topSpeedKmh;
    private String fuelType;
    private String transmission;

    @Column(length = 2000)
    private String description;

    public boolean isLowStock() {
        int threshold = lowStockThreshold == null ? 0 : lowStockThreshold;
        return stockQuantity != null && stockQuantity <= threshold;
    }
}
