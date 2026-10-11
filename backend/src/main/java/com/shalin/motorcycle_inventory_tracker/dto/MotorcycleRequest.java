package com.shalin.motorcycle_inventory_tracker.dto;

import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Create/update payload. The original seven fields keep their original rules and messages. */
public record MotorcycleRequest(
        @NotBlank(message = "Brand is required") String brand,
        @NotBlank(message = "Model is required") String model,
        @NotNull(message = "Manufacturing year is required")
        @Min(value = 1900, message = "Manufacturing year must be 1900 or later")
        @Max(value = 2100, message = "Manufacturing year must be 2100 or earlier") Integer manufacturingYear,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") BigDecimal price,
        @NotBlank(message = "Color is required") String color,
        @Min(value = 50, message = "Engine capacity must be at least 50 cc") Integer engineCapacityCc,
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative") Integer stockQuantity,
        @Min(value = 0, message = "Low-stock threshold cannot be negative") Integer lowStockThreshold,
        BikeCategory category,
        @DecimalMin(value = "0.1", message = "Motor power must be greater than 0 kW")
        @DecimalMax(value = "500", message = "Motor power must be 500 kW or less") BigDecimal motorPowerKw,
        @Size(max = 1000, message = "Photo URL is too long")
        @Pattern(regexp = "^(https?://.*|/.*)?$", message = "Photo URL must start with http(s):// or /") String photoUrl,
        @PositiveOrZero(message = "Power must be positive") Integer powerHp,
        @PositiveOrZero(message = "Torque must be positive") Integer torqueNm,
        @PositiveOrZero(message = "Weight must be positive") Integer weightKg,
        @PositiveOrZero(message = "Top speed must be positive") Integer topSpeedKmh,
        @Size(max = 40) String fuelType,
        @Size(max = 40) String transmission,
        @Size(max = 2000, message = "Description is too long") String description
) {

    /** Engine bikes need a cc figure; electric bikes are rated in kW instead. */
    @AssertTrue(message = "Engine capacity is required (electric bikes use motorPowerKw instead)")
    public boolean isEngineCapacityCcPresent() {
        return category == BikeCategory.ELECTRIC || engineCapacityCc != null;
    }

    @AssertTrue(message = "Motor power (kW) is required for electric bikes")
    public boolean isMotorPowerKwPresent() {
        return category != BikeCategory.ELECTRIC || motorPowerKw != null;
    }
}
