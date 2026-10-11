package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.SaleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.SaleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.mapper.SalesMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.service.impl.SaleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock SaleRepository saleRepo;
    @Mock MotorcycleRepository bikeRepo;

    SaleServiceImpl service() {
        return new SaleServiceImpl(saleRepo, bikeRepo, new SalesMapper(),
                Clock.fixed(Instant.parse("2026-10-11T10:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void saleDecrementsStockAndComputesTotal() {
        Motorcycle b = MotorcycleServiceImplTest.bike(1, 5);
        when(bikeRepo.findById(1L)).thenReturn(Optional.of(b));
        when(saleRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SaleResponse r = service().recordSale(new SaleRequest(1L, 2, null, "Aarav", null, null), "staff");

        assertThat(b.getStockQuantity()).isEqualTo(3);
        assertThat(r.totalPrice()).isEqualByComparingTo("622000");
        assertThat(r.soldBy()).isEqualTo("staff");
        assertThat(r.soldAt().getYear()).isEqualTo(2026);
    }

    @Test
    void saleRejectedWhenNotEnoughStock() {
        when(bikeRepo.findById(1L)).thenReturn(Optional.of(MotorcycleServiceImplTest.bike(1, 1)));
        assertThatThrownBy(() -> service().recordSale(new SaleRequest(1L, 2, new BigDecimal("1"), "A", null, null), "staff"))
                .isInstanceOf(BusinessException.class).hasMessageContaining("Only 1 unit");
        verify(saleRepo, never()).save(any());
    }
}
