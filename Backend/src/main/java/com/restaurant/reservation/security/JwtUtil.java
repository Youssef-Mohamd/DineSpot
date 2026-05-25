package com.restaurant.reservation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JWT Utility class for token generation, validation, and claims extraction
 * Uses HS256 algorithm (HMAC SHA-256) for token signing
 * Tokens expire after 24 hours
 */
@Component
public class JwtUtil {

  // JWT Configuration Constants
  private static final String SECRET_KEY = "restaurant_reservation_secret_key_2025_very_long_string";
  private static final long TOKEN_EXPIRATION_MS = 86400000; // 24 hours in milliseconds
  private static final String ROLE_CLAIM_NAME = "role";

  /**
   * Generates a signing key from the secret string
   * Uses HMAC SHA-256 algorithm for secure token signing
   *
   * @return cryptographic Key for signing JWTs
   */
  private Key generateSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  /**
   * Parses and validates JWT token, extracting claims from the payload
   * Verifies token signature before returning claims
   *
   * @param jwtToken the JWT token string
   * @return Claims object containing token payload data
   * @throws JwtException if token is invalid or signature verification fails
   */
  private Claims extractClaimsFromToken(String jwtToken) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(generateSigningKey())
          .build()
          .parseClaimsJws(jwtToken)
          .getBody();
    } catch (ExpiredJwtException e) {
      throw new JwtException("Token has expired", e);
    } catch (MalformedJwtException e) {
      throw new JwtException("Malformed JWT token", e);
    } catch (SignatureException e) {
      throw new JwtException("JWT signature verification failed", e);
    }
  }

  /**
   * Extracts the email (subject) claim from a valid JWT token
   *
   * @param jwtToken the JWT token string
   * @return the user's email address
   */
  public String extractEmail(String jwtToken) {
    return extractClaimsFromToken(jwtToken).getSubject();
  }

  /**
   * Extracts the role claim from a valid JWT token
   * Role is stored as a custom claim in the token payload
   *
   * @param jwtToken the JWT token string
   * @return the user's role
   */
  public String extractRole(String jwtToken) {
    return extractClaimsFromToken(jwtToken).get(ROLE_CLAIM_NAME, String.class);
  }

  /**
   * Validates JWT token signature and expiration status
   * Returns false if token is invalid, expired, or malformed
   *
   * @param jwtToken the JWT token string to validate
   * @return true if token is valid and not expired, false otherwise
   */
  public boolean isTokenValid(String jwtToken) {
    try {
      Claims tokenClaims = extractClaimsFromToken(jwtToken);
      Date expirationDate = tokenClaims.getExpiration();
      
      // Check if expiration date is in the future
      boolean isNotExpired = expirationDate != null && expirationDate.after(new Date());
      
      return isNotExpired;
    } catch (JwtException | IllegalArgumentException e) {
      return false; // Token is invalid or cannot be parsed
    }
  }

  /**
   * Generates a new JWT token with user credentials
   * Token includes email as subject and role as custom claim
   * Automatically sets issued-at and expiration times
   *
   * @param userEmail the user's email address (used as token subject)
   * @param userRole the user's role/authority level
   * @return signed JWT token string
   */
  public String generateToken(String userEmail, String userRole) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + TOKEN_EXPIRATION_MS);

    return Jwts.builder()
        .setSubject(userEmail)                    // Principal (user identity)
        .claim(ROLE_CLAIM_NAME, userRole)         // Custom role claim
        .setIssuedAt(now)                         // Token creation timestamp
        .setExpiration(expirationDate)            // Token expiration timestamp
        .signWith(generateSigningKey())           // Sign with secret key (HS256)
        .compact();                               // Serialize to JWT string
  }
}