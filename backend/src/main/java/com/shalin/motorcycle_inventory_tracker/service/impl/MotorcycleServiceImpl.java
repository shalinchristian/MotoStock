package com.shalin.motorcycle_inventory_tracker.service.impl;

import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.MotorcycleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.BikeCategory;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.exception.MotorcycleNotFoundException;
import com.shalin.motorcycle_inventory_tracker.mapper.MotorcycleMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import com.shalin.motorcycle_inventory_tracker.service.MotorcycleService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MotorcycleServiceImpl implements MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;
    private final SaleRepository saleRepository;
    private final TestRideRepository testRideRepository;
    private final MotorcycleMapper mapper;

    @Override
    public List<MotorcycleResponse> getAllMotorcycles() {
        return motorcycleRepository.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<MotorcycleResponse> search(String q, String brand, BikeCategory category, BigDecimal minPrice,
                                           BigDecimal maxPrice, Boolean inStock, String sort) {
        Specification<Motorcycle> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("model")), like),
                        cb.like(cb.lower(root.get("color")), like)));
            }
            if (brand != null && !brand.isBlank()) p.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
            if (category != null) p.add(cb.equal(root.get("category"), category));
            if (minPrice != null) p.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) p.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            if (Boolean.TRUE.equals(inStock)) p.add(cb.greaterThan(root.get("stockQuantity"), 0));
            return cb.and(p.toArray(Predicate[]::new));
        };
        return motorcycleRepository.findAll(spec, toSort(sort)).stream().map(mapper::toResponse).toList();
    }

    private static Sort toSort(String sort) {
        if (sort == null) return Sort.by("id");
        return switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "year_desc" -> Sort.by("manufacturingYear").descending();
            case "stock_asc" -> Sort.by("stockQuantity").ascending();
            case "name" -> Sort.by("brand", "model");
            default -> Sort.by("id");
        };
    }

    @Override
    public MotorcycleResponse getMotorcycleById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public MotorcycleResponse addMotorcycle(MotorcycleRequest request) {
        return mapper.toResponse(motorcycleRepository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public MotorcycleResponse updateMotorcycle(Long id, MotorcycleRequest request) {
        Motorcycle existing = find(id);
        mapper.apply(request, existing);
        return mapper.toResponse(motorcycleRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteMotorcycle(Long id) {
        Motorcycle existing = find(id);
        if (saleRepository.existsByMotorcycleId(id) || testRideRepository.existsByMotorcycleId(id)) {
            throw new BusinessException("Motorcycle has sales or test rides and cannot be deleted; set its stock to 0 instead");
        }
        motorcycleRepository.delete(existing);
    }

    @Override
    @Transactional
    public MotorcycleResponse adjustStock(Long id, int delta) {
        Motorcycle m = find(id);
        int next = m.getStockQuantity() + delta;
        if (next < 0) throw new BusinessException("Stock cannot go below zero (current: " + m.getStockQuantity() + ")");
        m.setStockQuantity(next);
        return mapper.toResponse(motorcycleRepository.save(m));
    }

    @Override
    public List<MotorcycleResponse> getLowStock() {
        return motorcycleRepository.findLowStock().stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<String> getBrands() {
        return motorcycleRepository.findDistinctBrands();
    }

    private Motorcycle find(Long id) {
        return motorcycleRepository.findById(id).orElseThrow(() -> new MotorcycleNotFoundException(id));
    }
}
