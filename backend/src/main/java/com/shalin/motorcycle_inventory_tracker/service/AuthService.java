package com.shalin.motorcycle_inventory_tracker.service;

import com.shalin.motorcycle_inventory_tracker.dto.LoginRequest;
import com.shalin.motorcycle_inventory_tracker.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
