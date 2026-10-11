package com.shalin.motorcycle_inventory_tracker.service.impl;

import com.shalin.motorcycle_inventory_tracker.dto.SaleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.SaleResponse;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.entity.Sale;
import com.shalin.motorcycle_inventory_tracker.exception.BusinessException;
import com.shalin.motorcycle_inventory_tracker.exception.MotorcycleNotFoundException;
import com.shalin.motorcycle_inventory_tracker.mapper.SalesMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final SalesMapper mapper;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAllByOrderBySoldAtDesc().stream().map(mapper::toResponse).toList();
    }

    /** Records a sale and decrements stock in the same transaction. */
    @Override
    @Transactional
    public SaleResponse recordSale(SaleRequest request, String soldBy) {
        Motorcycle bike = motorcycleRepository.findById(request.motorcycleId())
                .orElseThrow(() -> new MotorcycleNotFoundException(request.motorcycleId()));
        if (bike.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Only " + bike.getStockQuantity() + " unit(s) of " + SalesMapper.name(bike) + " in stock");
        }
        bike.setStockQuantity(bike.getStockQuantity() - request.quantity());
        motorcycleRepository.save(bike);

        BigDecimal unit = request.unitPrice() != null ? request.unitPrice() : bike.getPrice();
        Sale sale = new Sale();
        sale.setMotorcycle(bike);
        sale.setQuantity(request.quantity());
        sale.setUnitPrice(unit);
        sale.setTotalPrice(unit.multiply(BigDecimal.valueOf(request.quantity())));
        sale.setCustomerName(request.customerName().trim());
        sale.setCustomerPhone(request.customerPhone());
        sale.setCustomerEmail(request.customerEmail());
        sale.setSoldBy(soldBy);
        sale.setSoldAt(LocalDateTime.now(clock));
        return mapper.toResponse(saleRepository.save(sale));
    }
}
