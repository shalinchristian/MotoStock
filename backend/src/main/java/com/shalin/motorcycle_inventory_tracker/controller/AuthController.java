package com.shalin.motorcycle_inventory_tracker.controller;

import com.shalin.motorcycle_inventory_tracker.dto.LoginRequest;
import com.shalin.motorcycle_inventory_tracker.dto.LoginResponse;
import com.shalin.motorcycle_inventory_tracker.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("username", auth.getName(),
                "role", auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }
}
