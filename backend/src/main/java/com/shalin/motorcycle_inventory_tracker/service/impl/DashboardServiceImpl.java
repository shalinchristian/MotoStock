package com.shalin.motorcycle_inventory_tracker.service.impl;

import com.shalin.motorcycle_inventory_tracker.dto.DashboardResponse;
import com.shalin.motorcycle_inventory_tracker.dto.DashboardResponse.MonthlySales;
import com.shalin.motorcycle_inventory_tracker.entity.BookingStatus;
import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.entity.Sale;
import com.shalin.motorcycle_inventory_tracker.mapper.MotorcycleMapper;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import com.shalin.motorcycle_inventory_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final MotorcycleRepository motorcycleRepository;
    private final SaleRepository saleRepository;
    private final TestRideRepository testRideRepository;
    private final MotorcycleMapper mapper;
    private final Clock clock;

    @Override
    public DashboardResponse getDashboard() {
        List<Motorcycle> bikes = motorcycleRepository.findAll();
        long units = bikes.stream().mapToLong(Motorcycle::getStockQuantity).sum();
        BigDecimal value = bikes.stream()
                .map(b -> b.getPrice().multiply(BigDecimal.valueOf(b.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> byBrand = new TreeMap<>();
        Map<String, Long> byCategory = new TreeMap<>();
        for (Motorcycle b : bikes) {
            byBrand.merge(b.getBrand(), (long) b.getStockQuantity(), Long::sum);
            byCategory.merge(b.getCategory() == null ? "STANDARD" : b.getCategory().name(), (long) b.getStockQuantity(), Long::sum);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        YearMonth current = YearMonth.from(now);
        YearMonth first = current.minusMonths(5);
        List<Sale> recent = saleRepository.findBySoldAtBetweenOrderBySoldAtAsc(first.atDay(1).atStartOfDay(), now.plusDays(1));

        Map<YearMonth, long[]> units6 = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> revenue6 = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) { units6.put(first.plusMonths(i), new long[1]); revenue6.put(first.plusMonths(i), BigDecimal.ZERO); }
        for (Sale s : recent) {
            YearMonth ym = YearMonth.from(s.getSoldAt());
            if (!units6.containsKey(ym)) continue;
            units6.get(ym)[0] += s.getQuantity();
            revenue6.merge(ym, s.getTotalPrice(), BigDecimal::add);
        }
        List<MonthlySales> monthly = new ArrayList<>();
        units6.forEach((ym, u) -> monthly.add(new MonthlySales(ym.toString(), u[0], revenue6.get(ym))));
        MonthlySales thisMonth = monthly.get(monthly.size() - 1);

        var low = motorcycleRepository.findLowStock().stream().map(mapper::toResponse).toList();
        long upcoming = testRideRepository.countByStatusAndScheduledAtAfter(BookingStatus.SCHEDULED, now);

        return new DashboardResponse(bikes.size(), units, value, low.size(), thisMonth.units(), thisMonth.revenue(),
                upcoming, byBrand, byCategory, monthly, low);
    }
}
