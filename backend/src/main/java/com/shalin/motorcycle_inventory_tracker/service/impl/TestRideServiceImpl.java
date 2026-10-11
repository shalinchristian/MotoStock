package com.shalin.motorcycle_inventory_tracker.service.impl;

import com.shalin.motorcycle_inventory_tracker.dto.TestRideRequest;
import com.shalin.motorcycle_inventory_tracker.dto.TestRideResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.entity.TestRide;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.exception.MotorcycleNotFoundException;
import com.shalin.motorcycle_inventory_tracker.exception.ResourceNotFoundException;
import com.shalin.motorcycle_inventory_tracker.mapper.SalesMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import com.shalin.motorcycle_inventory_tracker.service.TestRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestRideServiceImpl implements TestRideService {

    private final TestRideRepository testRideRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final SalesMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TestRideResponse> getBookings(LocalDate from, LocalDate to) {
        List<TestRide> rides = (from != null && to != null)
                ? testRideRepository.findByScheduledAtBetweenOrderByScheduledAtAsc(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                : testRideRepository.findAllByOrderByScheduledAtAsc();
        return rides.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public TestRideResponse book(TestRideRequest r) {
        Motorcycle bike = motorcycleRepository.findById(r.motorcycleId())
                .orElseThrow(() -> new MotorcycleNotFoundException(r.motorcycleId()));
        int duration = r.durationMinutes() == null ? 30 : r.durationMinutes();
        LocalDateTime start = r.scheduledAt();
        LocalDateTime end = start.plusMinutes(duration);

        // one bike can't be on two test rides at once (look back 4h = max duration)
        boolean clash = testRideRepository
                .findByMotorcycleIdAndStatusAndScheduledAtBetween(bike.getId(), BookingStatus.SCHEDULED, start.minusHours(4), end)
                .stream()
                .anyMatch(t -> t.getScheduledAt().isBefore(end) && t.getScheduledAt().plusMinutes(t.getDurationMinutes()).isAfter(start));
        if (clash) throw new BusinessException("This bike already has a test ride booked in that slot");

        TestRide t = new TestRide();
        t.setMotorcycle(bike);
        t.setCustomerName(r.customerName().trim());
        t.setCustomerPhone(r.customerPhone());
        t.setCustomerEmail(r.customerEmail());
        t.setLicenseNumber(r.licenseNumber());
        t.setScheduledAt(start);
        t.setDurationMinutes(duration);
        t.setNotes(r.notes());
        t.setStatus(BookingStatus.SCHEDULED);
        return mapper.toResponse(testRideRepository.save(t));
    }

    @Override
    @Transactional
    public TestRideResponse updateStatus(Long id, BookingStatus status) {
        TestRide t = find(id);
        t.setStatus(status);
        return mapper.toResponse(testRideRepository.save(t));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        updateStatus(id, BookingStatus.CANCELLED);
    }

    private TestRide find(Long id) {
        return testRideRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Test ride", id));
    }
}
