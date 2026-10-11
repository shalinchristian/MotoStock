package com.shalin.motorcycle_inventory_tracker.mapper;

import com.shalin.motorcycle_inventory_tracker.dto.SaleResponse;
import com.shalin.motorcycle_inventory_tracker.dto.TestRideResponse;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.entity.Sale;
import com.shalin.motorcycle_inventory_tracker.entity.TestRide;
import org.springframework.stereotype.Component;

@Component
public class SalesMapper {

    public SaleResponse toResponse(Sale s) {
        return new SaleResponse(s.getId(), s.getMotorcycle().getId(), name(s.getMotorcycle()), s.getQuantity(),
                s.getUnitPrice(), s.getTotalPrice(), s.getCustomerName(), s.getCustomerPhone(), s.getCustomerEmail(),
                s.getSoldBy(), s.getSoldAt());
    }

    public TestRideResponse toResponse(TestRide t) {
        return new TestRideResponse(t.getId(), t.getMotorcycle().getId(), name(t.getMotorcycle()), t.getCustomerName(),
                t.getCustomerPhone(), t.getCustomerEmail(), t.getLicenseNumber(), t.getScheduledAt(),
                t.getDurationMinutes(), t.getStatus(), t.getNotes());
    }

    public static String name(Motorcycle m) {
        return m.getBrand() + " " + m.getModel() + " (" + m.getManufacturingYear() + ")";
    }
}
