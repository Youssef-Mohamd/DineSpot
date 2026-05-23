package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.*;
import com.restaurant.reservation.dto.response.*;
import com.restaurant.reservation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Inject AuthService to handle business logic
    private final AuthService authService;

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
       // @Valid → triggers validation on DTO (email format, password length,...)
        // Return 201 => CREATED
        return ResponseEntity.status(201)
                .body(authService.register(req));
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        // Validate credentials and return JWT token
        return ResponseEntity.ok(authService.login(req));
    }

    // ================= GET CURRENT USER =================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {

        return ResponseEntity.ok(
                authService.getMe(auth.getName())  // auth.getName() → returns email from JWT token
        );
    }
}