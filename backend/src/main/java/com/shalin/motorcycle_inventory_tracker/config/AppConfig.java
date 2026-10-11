package com.shalin.motorcycle_inventory_tracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@OpenAPIDefinition(info = @Info(title = "MotoStock API", version = "1.0",
        description = "Motorcycle dealership management: inventory, stock alerts, sales and test rides"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class AppConfig {

    /** Injectable clock so services/tests don't depend on wall time. */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
