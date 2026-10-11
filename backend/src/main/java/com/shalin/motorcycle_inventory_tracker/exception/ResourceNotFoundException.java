package com.shalin.motorcycle_inventory_tracker.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String what, Long id) { super(what + " not found with id: " + id); }
}
