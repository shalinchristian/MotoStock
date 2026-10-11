package com.shalin.motorcycle_inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_rides")
@Data
@NoArgsConstructor
public class TestRide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Motorcycle motorcycle;

    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String customerPhone;
    private String customerEmail;
    private String licenseNumber;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private Integer durationMinutes = 30;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.SCHEDULED;

    @Column(length = 1000)
    private String notes;
}
