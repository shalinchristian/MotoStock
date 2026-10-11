package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.TestRideRequest;
import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;
import com.shalin.motorcycle_inventory_tracker.entity.TestRide;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.mapper.SalesMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import com.shalin.motorcycle_inventory_tracker.service.impl.TestRideServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRideServiceImplTest {

    @Mock TestRideRepository rideRepo;
    @Mock MotorcycleRepository bikeRepo;

    @Test
    void overlappingBookingIsRejected() {
        var bike = MotorcycleServiceImplTest.bike(1, 3);
        LocalDateTime slot = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        TestRide existing = new TestRide();
        existing.setMotorcycle(bike); existing.setScheduledAt(slot.minusMinutes(15)); existing.setDurationMinutes(30);
        when(bikeRepo.findById(1L)).thenReturn(Optional.of(bike));
        when(rideRepo.findByMotorcycleIdAndStatusAndScheduledAtBetween(eq(1L), eq(BookingStatus.SCHEDULED), any(), any()))
                .thenReturn(List.of(existing));

        var service = new TestRideServiceImpl(rideRepo, bikeRepo, new SalesMapper());
        assertThatThrownBy(() -> service.book(new TestRideRequest(1L, "Diya", "9825012345", null, null, slot, 30, null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void freeSlotIsBooked() {
        var bike = MotorcycleServiceImplTest.bike(1, 3);
        LocalDateTime slot = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        when(bikeRepo.findById(1L)).thenReturn(Optional.of(bike));
        when(rideRepo.findByMotorcycleIdAndStatusAndScheduledAtBetween(anyLong(), any(), any(), any())).thenReturn(List.of());
        when(rideRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var r = new TestRideServiceImpl(rideRepo, bikeRepo, new SalesMapper())
                .book(new TestRideRequest(1L, "Diya", "9825012345", null, null, slot, null, null));
        assertThat(r.status()).isEqualTo(BookingStatus.SCHEDULED);
        assertThat(r.durationMinutes()).isEqualTo(30);
    }
}
