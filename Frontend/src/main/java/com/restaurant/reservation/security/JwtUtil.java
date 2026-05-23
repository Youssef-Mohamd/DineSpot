package com.restaurant.reservation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key used to sign the token (should be moved to application.properties in production)
    private static final String SECRET = "restaurant_reservation_secret_key_2025_very_long_string";

    // Token expiration time (24 hours)
    private static final long EXPIRATION = 86400000;

    // Generate signing key from secret
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ================= GENERATE TOKEN =================
    // Creates JWT token containing email and role
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) // main identity (user email)
                .claim("role", role) // custom claim (user role)
                .setIssuedAt(new Date()) // token creation time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // expiration time
                .signWith(getKey()) // sign token with secret key
                .compact();
    }

    // ================= EXTRACT EMAIL =================
    // Extract email (subject) from token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ================= EXTRACT ROLE =================
    // Extract role from token claims
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ================= VALIDATE TOKEN =================
    // Check if token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date()); // check expiration
        } catch (JwtException e) {
            return false; // invalid token
        }
    }

    // ================= GET CLAIMS =================
    // Parse token and return claims (payload)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // verify signature
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}