package com.shalin.motorcycle_inventory_tracker.exception;

/** A request that is well-formed but breaks a business rule (e.g. not enough stock). Maps to 409. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
