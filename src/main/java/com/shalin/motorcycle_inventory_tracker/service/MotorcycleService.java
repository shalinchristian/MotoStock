package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.entity.Motorcycle;
import com.shalin.motorcycle_inventory_tracker.exception.MotorcycleNotFoundException;
import com.shalin.motorcycle_inventory_tracker.repository.MotorcycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;

    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleRepository.findAll();
    }

    public Motorcycle getMotorcycleById(Long id) {
        return motorcycleRepository.findById(id)
                .orElseThrow(() -> new MotorcycleNotFoundException(id));
    }

    public Motorcycle addMotorcycle(Motorcycle motorcycle) {
        return motorcycleRepository.save(motorcycle);
    }

    public Motorcycle updateMotorcycle(Long id, Motorcycle motorcycleDetails) {
        Motorcycle existingMotorcycle = getMotorcycleById(id);

        existingMotorcycle.setBrand(motorcycleDetails.getBrand());
        existingMotorcycle.setModel(motorcycleDetails.getModel());
        existingMotorcycle.setManufacturingYear(motorcycleDetails.getManufacturingYear());
        existingMotorcycle.setPrice(motorcycleDetails.getPrice());
        existingMotorcycle.setColor(motorcycleDetails.getColor());
        existingMotorcycle.setEngineCapacityCc(motorcycleDetails.getEngineCapacityCc());
        existingMotorcycle.setStockQuantity(motorcycleDetails.getStockQuantity());

        return motorcycleRepository.save(existingMotorcycle);
    }

    public void deleteMotorcycle(Long id) {
        Motorcycle existingMotorcycle = getMotorcycleById(id);
        motorcycleRepository.delete(existingMotorcycle);
    }
}
