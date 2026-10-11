package com.shalin.motorcycle_inventory_tracker.config;

import com.shalin.motorcycle_inventory_tracker.entity.*;
import com.shalin.motorcycle_inventory_tracker.repository.AppUserRepository;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.SaleRepository;
import com.shalin.motorcycle_inventory_tracker.repository.TestRideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** Inserts demo users, bikes, sales and test rides when the tables are empty. Disable with SEED_DATA=false. */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository users;
    private final MotorcycleRepository bikes;
    private final SaleRepository sales;
    private final TestRideRepository rides;
    private final PasswordEncoder encoder;
    private final Clock clock;

    @Value("${app.seed.admin-password}") private String adminPassword;
    @Value("${app.seed.staff-password}") private String staffPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!users.existsByUsername("admin")) {
            users.save(user("admin", adminPassword, "Shalin Christian", Role.ADMIN));
            users.save(user("staff", staffPassword, "Showroom Staff", Role.STAFF));
            log.info("Seeded users 'admin' and 'staff'");
        }
        if (bikes.count() > 0) return;

        List<Motorcycle> list = bikes.saveAll(List.of(
            bike("Royal Enfield", "Classic 350", 2024, "193000", "Signals Marsh Grey", 349, 12, BikeCategory.CRUISER, 20, 27, 195, 114, "Retro single-cylinder cruiser, the showroom favourite."),
            bike("Royal Enfield", "Himalayan 450", 2024, "285000", "Kaza Brown", 452, 4, BikeCategory.ADVENTURE, 40, 40, 196, 140, "Liquid-cooled adventure tourer with long-travel suspension."),
            bike("Bajaj", "Pulsar NS200", 2023, "150000", "Pewter Grey", 199, 9, BikeCategory.SPORT, 24, 18, 159, 136, "Naked street bike with perimeter frame."),
            bike("TVS", "Apache RTR 310", 2024, "243000", "Fury Yellow", 312, 2, BikeCategory.SPORT, 35, 28, 169, 150, "Ride modes, quickshifter and cornering ABS."),
            bike("KTM", "390 Duke", 2024, "311000", "Electronic Orange", 399, 1, BikeCategory.SPORT, 45, 39, 168, 165, "Lightweight naked with a 399 cc LC4c engine."),
            bike("Honda", "CB350 H'ness", 2023, "209000", "Pearl Siren Blue", 348, 6, BikeCategory.STANDARD, 21, 30, 181, 130, "Smooth long-stroke retro roadster."),
            bike("Yamaha", "R15 V4", 2024, "182000", "Racing Blue", 155, 0, BikeCategory.SPORT, 18, 14, 142, 140, "Track-inspired supersport with VVA."),
            bike("Suzuki", "V-Strom SX", 2023, "211000", "Champion Yellow", 249, 3, BikeCategory.TOURING, 26, 22, 167, 135, "Comfortable sport-tourer for long highway runs."),
            bike("Ather", "450X", 2024, "149000", "Space Grey", 0, 7, BikeCategory.ELECTRIC, 8, 26, 108, 90, "Connected electric scooter, 150 km claimed range."),
            bike("Hero", "Xpulse 200 4V", 2023, "151000", "Trail Blue", 199, 5, BikeCategory.DIRT, 19, 17, 159, 115, "Affordable off-roader with 220 mm ground clearance.")
        ));
        list.get(8).setEngineCapacityCc(null);      // electric: rated in kW, not cc
        list.get(8).setMotorPowerKw(new BigDecimal("6.4"));
        list.get(8).setFuelType("Electric");
        list.get(8).setTransmission("Automatic");
        bikes.saveAll(list);

        LocalDateTime now = LocalDateTime.now(clock).withHour(11).withMinute(0).withSecond(0).withNano(0);
        int[][] plan = {{0, 3}, {2, 1}, {5, 2}, {1, 1}, {0, 2}, {3, 1}, {9, 2}, {7, 1}, {0, 1}, {2, 2}, {5, 1}, {6, 1}};
        String[] customers = {"Aarav Patel", "Diya Shah", "Kabir Mehta", "Ishita Rao", "Vihaan Joshi", "Ananya Iyer"};
        for (int i = 0; i < plan.length; i++) {
            Motorcycle b = list.get(plan[i][0]);
            Sale s = new Sale();
            s.setMotorcycle(b);
            s.setQuantity(plan[i][1]);
            s.setUnitPrice(b.getPrice());
            s.setTotalPrice(b.getPrice().multiply(BigDecimal.valueOf(plan[i][1])));
            s.setCustomerName(customers[i % customers.length]);
            s.setCustomerPhone("98250" + String.format("%05d", 10000 + i));
            s.setSoldBy(i % 2 == 0 ? "admin" : "staff");
            s.setSoldAt(now.minusDays(i * 14L + 1));
            sales.save(s);
        }
        for (int i = 0; i < 6; i++) {
            TestRide t = new TestRide();
            t.setMotorcycle(list.get(i));
            t.setCustomerName(customers[(i + 2) % customers.length]);
            t.setCustomerPhone("99099" + String.format("%05d", 20000 + i));
            t.setScheduledAt(now.plusDays(i + 1).withHour(10 + i));
            t.setDurationMinutes(30);
            t.setStatus(BookingStatus.SCHEDULED);
            rides.save(t);
        }
        log.info("Seeded {} motorcycles, {} sales, {} test rides", list.size(), plan.length, 6);
    }

    private AppUser user(String username, String password, String fullName, Role role) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        u.setFullName(fullName);
        u.setRole(role);
        return u;
    }

    private static Motorcycle bike(String brand, String model, int year, String price, String color, int cc, int stock,
                                   BikeCategory cat, int hp, int nm, int kg, int top, String desc) {
        Motorcycle m = new Motorcycle();
        m.setBrand(brand); m.setModel(model); m.setManufacturingYear(year); m.setPrice(new BigDecimal(price));
        m.setColor(color); m.setEngineCapacityCc(cc); m.setStockQuantity(stock); m.setLowStockThreshold(2);
        m.setCategory(cat); m.setPhotoUrl("/img/" + cat.name().toLowerCase() + ".svg");
        m.setPowerHp(hp); m.setTorqueNm(nm); m.setWeightKg(kg); m.setTopSpeedKmh(top);
        m.setFuelType("Petrol"); m.setTransmission(cat == BikeCategory.ELECTRIC ? "Automatic" : "Manual");
        m.setDescription(desc);
        return m;
    }
}
