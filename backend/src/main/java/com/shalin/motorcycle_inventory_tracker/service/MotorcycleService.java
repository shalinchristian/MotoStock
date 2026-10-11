package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;

import java.math.BigDecimal;
import java.util.List;

public interface MotorcycleService {
    List<MotorcycleResponse> getAllMotorcycles();
    List<MotorcycleResponse> search(String q, String brand, BikeCategory category, BigDecimal minPrice,
                                    BigDecimal maxPrice, Boolean inStock, String sort);
    MotorcycleResponse getMotorcycleById(Long id);
    MotorcycleResponse addMotorcycle(MotorcycleRequest request);
    MotorcycleResponse updateMotorcycle(Long id, MotorcycleRequest request);
    void deleteMotorcycle(Long id);
    MotorcycleResponse adjustStock(Long id, int delta);
    List<MotorcycleResponse> getLowStock();
    List<String> getBrands();
}
