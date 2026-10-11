package com.shalin.motorcycle_inventory_tracker.service.impl;

import com.shalin.motorcycle_inventory_tracker.dto.LoginRequest;
import com.shalin.motorcycle_inventory_tracker.dto.LoginResponse;
import com.shalin.motorcycle_inventory_tracker.entity.AppUser;
import com.shalin.motorcycle_inventory_tracker.repository.AppUserRepository;
import com.shalin.motorcycle_inventory_tracker.security.JwtService;
import com.shalin.motorcycle_inventory_tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username().trim())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("bad credentials"));
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getFullName(), user.getRole().name(), jwtService.getExpirationSeconds());
    }
}
