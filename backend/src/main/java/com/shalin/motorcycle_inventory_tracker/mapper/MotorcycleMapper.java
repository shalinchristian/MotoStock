package com.shalin.motorcycle_inventory_tracker.mapper;

import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import org.springframework.stereotype.Component;

@Component
public class MotorcycleMapper {

    public Motorcycle toEntity(MotorcycleRequest r) {
        Motorcycle m = new Motorcycle();
        apply(r, m);
        return m;
    }

    /** Copies every request field onto an existing entity (full update, like the old PUT). */
    public void apply(MotorcycleRequest r, Motorcycle m) {
        m.setBrand(r.brand().trim());
        m.setModel(r.model().trim());
        m.setManufacturingYear(r.manufacturingYear());
        m.setPrice(r.price());
        m.setColor(r.color().trim());
        BikeCategory category = r.category() == null ? BikeCategory.STANDARD : r.category();
        boolean electric = category == BikeCategory.ELECTRIC;
        m.setCategory(category);
        m.setEngineCapacityCc(electric ? null : r.engineCapacityCc());
        m.setMotorPowerKw(electric ? r.motorPowerKw() : null);
        m.setStockQuantity(r.stockQuantity());
        m.setLowStockThreshold(r.lowStockThreshold() == null ? 2 : r.lowStockThreshold());
        m.setPhotoUrl(blankToNull(r.photoUrl()));
        m.setPowerHp(r.powerHp());
        m.setTorqueNm(r.torqueNm());
        m.setWeightKg(r.weightKg());
        m.setTopSpeedKmh(r.topSpeedKmh());
        m.setFuelType(blankToNull(r.fuelType()));
        m.setTransmission(blankToNull(r.transmission()));
        m.setDescription(blankToNull(r.description()));
    }

    public MotorcycleResponse toResponse(Motorcycle m) {
        return new MotorcycleResponse(m.getId(), m.getBrand(), m.getModel(), m.getManufacturingYear(), m.getPrice(),
                m.getColor(), m.getEngineCapacityCc(), m.getMotorPowerKw(), m.getStockQuantity(), m.getLowStockThreshold(), m.isLowStock(),
                m.getCategory(), m.getPhotoUrl(), m.getPowerHp(), m.getTorqueNm(), m.getWeightKg(), m.getTopSpeedKmh(),
                m.getFuelType(), m.getTransmission(), m.getDescription());
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
