package com.shalin.motorcycle_inventory_tracker.repository;

import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long>, JpaSpecificationExecutor<Motorcycle> {

    @Query("select m from Motorcycle m where m.stockQuantity <= coalesce(m.lowStockThreshold, 0) order by m.stockQuantity asc")
    List<Motorcycle> findLowStock();

    @Query("select distinct m.brand from Motorcycle m order by m.brand")
    List<String> findDistinctBrands();
}
