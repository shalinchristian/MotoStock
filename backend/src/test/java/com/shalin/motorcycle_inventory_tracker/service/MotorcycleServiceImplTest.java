package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.exception.MotorcycleNotFoundException;
import com.shalin.motorcycle_inventory_tracker.mapper.MotorcycleMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import com.shalin.motorcycle_inventory_tracker.service.impl.MotorcycleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MotorcycleServiceImplTest {

    @Mock MotorcycleRepository repo;
    @Mock SaleRepository saleRepo;
    @Mock TestRideRepository rideRepo;
    MotorcycleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MotorcycleServiceImpl(repo, saleRepo, rideRepo, new MotorcycleMapper());
    }

    static Motorcycle bike(long id, int stock) {
        Motorcycle m = new Motorcycle();
        m.setId(id); m.setBrand("KTM"); m.setModel("390 Duke"); m.setManufacturingYear(2024);
        m.setPrice(new BigDecimal("311000")); m.setColor("Orange"); m.setEngineCapacityCc(399);
        m.setStockQuantity(stock); m.setLowStockThreshold(2);
        return m;
    }

    static MotorcycleRequest request(int stock) {
        return new MotorcycleRequest("Honda", "CB350", 2023, new BigDecimal("209000"), "Blue", 348, stock,
                null, BikeCategory.STANDARD, null, "/img/standard.svg", 21, 30, 181, 130, "Petrol", "Manual", "  ");
    }

    @Test
    void addMapsRequestAndAppliesDefaults() {
        when(repo.save(any())).thenAnswer(inv -> { Motorcycle m = inv.getArgument(0); m.setId(7L); return m; });
        MotorcycleResponse r = service.addMotorcycle(request(5));
        assertThat(r.id()).isEqualTo(7L);
        assertThat(r.brand()).isEqualTo("Honda");
        assertThat(r.lowStockThreshold()).isEqualTo(2);
        assertThat(r.description()).isNull();
        assertThat(r.lowStock()).isFalse();
    }

    @Test
    void electricBikeDropsCcAndKeepsKw() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var req = new MotorcycleRequest("Ather", "450X", 2024, new BigDecimal("149000"), "Grey", 50, 3,
                null, BikeCategory.ELECTRIC, new BigDecimal("6.4"), null, null, null, null, null, "Electric", "Automatic", null);
        MotorcycleResponse r = service.addMotorcycle(req);
        assertThat(r.engineCapacityCc()).isNull();
        assertThat(r.motorPowerKw()).isEqualByComparingTo("6.4");
    }

    @Test
    void lowStockFlagWhenAtThreshold() {
        when(repo.findById(1L)).thenReturn(Optional.of(bike(1, 2)));
        assertThat(service.getMotorcycleById(1L).lowStock()).isTrue();
    }

    @Test
    void getMissingThrowsNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getMotorcycleById(99L))
                .isInstanceOf(MotorcycleNotFoundException.class)
                .hasMessage("Motorcycle not found with id: 99");
    }

    @Test
    void updateOverwritesFields() {
        Motorcycle existing = bike(1, 1);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        MotorcycleResponse r = service.updateMotorcycle(1L, request(10));
        assertThat(r.brand()).isEqualTo("Honda");
        assertThat(r.stockQuantity()).isEqualTo(10);
    }

    @Test
    void adjustStockRejectsNegativeResult() {
        when(repo.findById(1L)).thenReturn(Optional.of(bike(1, 1)));
        assertThatThrownBy(() -> service.adjustStock(1L, -2)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void adjustStockAddsDelta() {
        Motorcycle m = bike(1, 1);
        when(repo.findById(1L)).thenReturn(Optional.of(m));
        when(repo.save(m)).thenReturn(m);
        assertThat(service.adjustStock(1L, 4).stockQuantity()).isEqualTo(5);
    }

    @Test
    void deleteBlockedWhenBikeHasSales() {
        when(repo.findById(1L)).thenReturn(Optional.of(bike(1, 1)));
        when(saleRepo.existsByMotorcycleId(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.deleteMotorcycle(1L)).isInstanceOf(BusinessException.class);
        verify(repo, never()).delete(any(Motorcycle.class));
    }
}
