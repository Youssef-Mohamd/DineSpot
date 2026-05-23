
package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.*;
import com.restaurant.reservation.dto.response.*;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.UserRepository;
import com.restaurant.reservation.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    // Repository to access user data from database
    private final UserRepository userRepository;

    // Used to hash passwords securely
    private final PasswordEncoder passwordEncoder;

    // Utility class for generating and parsing JWT tokens
    private final JwtUtil jwtUtil;

    // ================= REGISTER =================
    // Register a new user and return JWT token
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        // Check if email already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user object
        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                // Encrypt password before saving
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                // Default role for new users
                .role(Role.CUSTOMER)
                // Set active status
                .isActive(true)
                .build();

        // Save user in database
        userRepository.save(user);

        // Generate JWT token for the new user
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Return response with token and user info
        return buildAuthResponse(user, token);
    }

    // ================= LOGIN =================
    // Authenticate user and return JWT token
    public AuthResponse login(LoginRequest req) {

        // Fetch user by email
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if password matches hashed password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT token after successful login
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Return response with token and user info
        return buildAuthResponse(user, token);
    }

    // ================= GET CURRENT USER =================
    // Get current logged-in user info using email
    public UserResponse getMe(String email) {

        // Fetch user from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert entity to response DTO
        return mapToResponse(user);
    }

    // ================= ADMIN VALIDATION =================
    // Validate if user has ADMIN role
    public void validateAdminRole(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Access denied: Admin role required");
        }
    }

    // ================= HELPERS =================

    // Build authentication response (used in register & login)
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token) // JWT token
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // Convert User entity to UserResponse DTO
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}