package com.shalin.motorcycle_inventory_tracker.repository;

import com.shalin.motorcycle_inventory_tracker.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOrderBySoldAtDesc();
    List<Sale> findBySoldAtBetweenOrderBySoldAtAsc(LocalDateTime from, LocalDateTime to);
    boolean existsByMotorcycleId(Long motorcycleId);
}
