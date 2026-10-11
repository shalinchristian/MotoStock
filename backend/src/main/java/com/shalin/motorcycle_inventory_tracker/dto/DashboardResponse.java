package com.shalin.motorcycle_inventory_tracker.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalModels, long unitsInStock, BigDecimal inventoryValue, long lowStockCount,
        long salesThisMonth, BigDecimal revenueThisMonth, long upcomingTestRides,
        Map<String, Long> stockByBrand, Map<String, Long> stockByCategory,
        List<MonthlySales> salesByMonth, List<MotorcycleResponse> lowStock
) {
    public record MonthlySales(String month, long units, BigDecimal revenue) {}
}
