package com.shalin.motorcycle_inventory_tracker.exception;

public class MotorcycleNotFoundException extends RuntimeException {

    public MotorcycleNotFoundException(Long id) {
        super("Motorcycle not found with id: " + id);
    }
}
