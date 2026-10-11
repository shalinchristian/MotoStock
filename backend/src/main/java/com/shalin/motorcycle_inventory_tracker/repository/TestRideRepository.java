package com.shalin.motorcycle_inventory_tracker.repository;

import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;
import com.shalin.motorcycle_inventory_tracker.entity.TestRide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TestRideRepository extends JpaRepository<TestRide, Long> {
    List<TestRide> findByScheduledAtBetweenOrderByScheduledAtAsc(LocalDateTime from, LocalDateTime to);
    List<TestRide> findAllByOrderByScheduledAtAsc();
    List<TestRide> findByMotorcycleIdAndStatusAndScheduledAtBetween(Long motorcycleId, BookingStatus status, LocalDateTime from, LocalDateTime to);
    long countByStatusAndScheduledAtAfter(BookingStatus status, LocalDateTime after);
    boolean existsByMotorcycleId(Long motorcycleId);
}
