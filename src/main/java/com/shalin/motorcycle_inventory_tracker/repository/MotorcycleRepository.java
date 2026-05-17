package com.shalin.motorcycle_inventory_tracker.repository;

import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {
}
