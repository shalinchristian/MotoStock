package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.SaleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.SaleResponse;

import java.util.List;

public interface SaleService {
    List<SaleResponse> getAllSales();
    SaleResponse recordSale(SaleRequest request, String soldBy);
}
