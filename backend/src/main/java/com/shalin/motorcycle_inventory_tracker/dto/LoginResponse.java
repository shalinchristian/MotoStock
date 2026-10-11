package com.shalin.motorcycle_inventory_tracker.dto;

public record LoginResponse(String token, String username, String fullName, String role, long expiresInSeconds) {}
