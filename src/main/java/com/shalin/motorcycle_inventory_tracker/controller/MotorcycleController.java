package com.shalin.motorcycle_inventory_tracker.controller;

import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.service.MotorcycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/motorcycles")
@RequiredArgsConstructor
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    @GetMapping
    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleService.getAllMotorcycles();
    }

    @GetMapping("/{id}")
    public Motorcycle getMotorcycleById(@PathVariable Long id) {
        return motorcycleService.getMotorcycleById(id);
    }

    @PostMapping
    public ResponseEntity<Motorcycle> addMotorcycle(@Valid @RequestBody Motorcycle motorcycle) {
        Motorcycle savedMotorcycle = motorcycleService.addMotorcycle(motorcycle);
        return new ResponseEntity<>(savedMotorcycle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Motorcycle updateMotorcycle(@PathVariable Long id, @Valid @RequestBody Motorcycle motorcycle) {
        return motorcycleService.updateMotorcycle(id, motorcycle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMotorcycle(@PathVariable Long id) {
        motorcycleService.deleteMotorcycle(id);
        return ResponseEntity.noContent().build();
    }
}
