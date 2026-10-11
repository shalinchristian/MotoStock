package com.shalin.motorcycle_inventory_tracker.controller;

import com.shalin.motorcycle_inventory_tracker.dto.StatusUpdateRequest;
import com.shalin.motorcycle_inventory_tracker.dto.TestRideRequest;
import com.shalin.motorcycle_inventory_tracker.dto.TestRideResponse;
import com.shalin.motorcycle_inventory_tracker.service.TestRideService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/test-rides")
@RequiredArgsConstructor
@Tag(name = "Test rides")
@SecurityRequirement(name = "bearerAuth")
public class TestRideController {

    private final TestRideService testRideService;

    @GetMapping
    public List<TestRideResponse> getBookings(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return testRideService.getBookings(from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestRideResponse book(@Valid @RequestBody TestRideRequest request) {
        return testRideService.book(request);
    }

    @PatchMapping("/{id}/status")
    public TestRideResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return testRideService.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        testRideService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
