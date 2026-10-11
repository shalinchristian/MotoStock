package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.TestRideRequest;
import com.shalin.motorcycle_inventory_tracker.dto.TestRideResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface TestRideService {
    List<TestRideResponse> getBookings(LocalDate from, LocalDate to);
    TestRideResponse book(TestRideRequest request);
    TestRideResponse updateStatus(Long id, BookingStatus status);
    void cancel(Long id);
}
