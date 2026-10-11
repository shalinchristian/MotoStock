package com.shalin.motorcycle_inventory_tracker.controller;

import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleResponse;
import com.shalin.motorcycle_inventory_tracker.dto.StockAdjustmentRequest;
import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;
import com.shalin.motorcycle_inventory_tracker.service.MotorcycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** Original inventory API (same paths and verbs), plus search, stock and low-stock endpoints. */
@RestController
@RequestMapping("/api/motorcycles")
@RequiredArgsConstructor
@Tag(name = "Motorcycles")
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    @GetMapping
    @Operation(summary = "List bikes; all filters optional (sort: price_asc, price_desc, year_desc, stock_asc, name)")
    public List<MotorcycleResponse> getAllMotorcycles(@RequestParam(required = false) String q,
                                                      @RequestParam(required = false) String brand,
                                                      @RequestParam(required = false) BikeCategory category,
                                                      @RequestParam(required = false) BigDecimal minPrice,
                                                      @RequestParam(required = false) BigDecimal maxPrice,
                                                      @RequestParam(required = false) Boolean inStock,
                                                      @RequestParam(required = false) String sort) {
        if (q == null && brand == null && category == null && minPrice == null && maxPrice == null && inStock == null && sort == null) {
            return motorcycleService.getAllMotorcycles();
        }
        return motorcycleService.search(q, brand, category, minPrice, maxPrice, inStock, sort);
    }

    @GetMapping("/{id}")
    public MotorcycleResponse getMotorcycleById(@PathVariable Long id) {
        return motorcycleService.getMotorcycleById(id);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Bikes at or below their low-stock threshold")
    public List<MotorcycleResponse> getLowStock() {
        return motorcycleService.getLowStock();
    }

    @GetMapping("/brands")
    public List<String> getBrands() {
        return motorcycleService.getBrands();
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MotorcycleResponse> addMotorcycle(@Valid @RequestBody MotorcycleRequest motorcycle) {
        return new ResponseEntity<>(motorcycleService.addMotorcycle(motorcycle), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public MotorcycleResponse updateMotorcycle(@PathVariable Long id, @Valid @RequestBody MotorcycleRequest motorcycle) {
        return motorcycleService.updateMotorcycle(id, motorcycle);
    }

    @PatchMapping("/{id}/stock")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Restock (+) or write off (-) units")
    public MotorcycleResponse adjustStock(@PathVariable Long id, @Valid @RequestBody StockAdjustmentRequest request) {
        return motorcycleService.adjustStock(id, request.delta());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a bike (ADMIN only)")
    public ResponseEntity<Void> deleteMotorcycle(@PathVariable Long id) {
        motorcycleService.deleteMotorcycle(id);
        return ResponseEntity.noContent().build();
    }
}
