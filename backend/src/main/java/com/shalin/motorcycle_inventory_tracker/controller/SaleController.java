package com.shalin.motorcycle_inventory_tracker.controller;

import com.shalin.motorcycle_inventory_tracker.dto.SaleRequest;
import com.shalin.motorcycle_inventory_tracker.dto.SaleResponse;
import com.shalin.motorcycle_inventory_tracker.service.SaleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public List<SaleResponse> getSales() {
        return saleService.getAllSales();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse recordSale(@Valid @RequestBody SaleRequest request, Authentication auth) {
        return saleService.recordSale(request, auth.getName());
    }
}
