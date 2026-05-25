package com.restaurant.reservation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JWT Utility service for managing JSON Web Tokens
 * Handles token generation, validation, and claims extraction
 * Token Algorithm: HS256 (HMAC SHA-256)
 * Token Lifespan: 24 hours
 */
@Component
public class JwtUtil {

  // ============= TOKEN CONFIGURATION =============
  private static final String JWT_SECRET = "restaurant_reservation_secret_key_2025_very_long_string";
  private static final long EXPIRATION_TIME_MS = 86400000; // 24 hours
  private static final String ROLE = "role";

  // ============= KEY GENERATION =============
  /**
   * Creates cryptographic signing key from secret string
   * Algorithm: HMAC SHA-256
   * Used for signing and verifying JWT tokens
   */
  private Key createSigningKey() {
    byte[] secretBytes = JWT_SECRET.getBytes();
    return Keys.hmacShaKeyFor(secretBytes);
  }

  // ============= CLAIM EXTRACTION =============
  /**
   * Parses and extracts claims from JWT token
   * Validates signature during parsing process
   *
   * @param token JWT token string
   * @return Claims from token payload
   * @throws JwtException if token validation fails
   */
  private Claims parseTokenToClaims(String token) {
    try {
      JwtParser tokenParser = Jwts.parserBuilder()
          .setSigningKey(createSigningKey())
          .build();

      return tokenParser
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException expiredException) {
      throw new JwtException("JWT token has expired", expiredException);
    } catch (MalformedJwtException malformedException) {
      throw new JwtException("JWT token is malformed or invalid format", malformedException);
    } catch (SignatureException signatureException) {
      throw new JwtException("JWT signature validation failed", signatureException);
    }
  }

  /**
   * Extracts user email (subject claim) from token
   *
   * @param token JWT token string
   * @return email address stored as subject
   */
  public String extractUserEmail(String token) {
    Claims claims = parseTokenToClaims(token);
    return claims.getSubject();
  }

  /**
   * Extracts user role from custom claim in token
   *
   * @param token JWT token string
   * @return user role value
   */
  public String extractUserRole(String token) {
    Claims claims = parseTokenToClaims(token);
    return claims.get(ROLE, String.class);
  }

  // ============= TOKEN GENERATION =============
  /**
   * Creates and signs a new JWT token for user
   * Embeds email as subject and role as custom claim
   * Automatically timestamps creation and expiration
   *
   * @param email user's email (becomes token subject/principal)
   * @param role user's role/authority
   * @return signed JWT token string
   */
  public String createToken(String email, String role) {
    Date issuedAtTime = new Date();
    Date expiresAtTime = calculateTokenExpirationTime(issuedAtTime);

    // Build JWT with JJWT library
    String jwtString = Jwts.builder()
        .setSubject(email)                              // User identity
        .claim(ROLE, role)                              // User authority
        .setIssuedAt(issuedAtTime)                      // Creation time
        .setExpiration(expiresAtTime)                   // Expiration time
        .signWith(createSigningKey())                   // Sign with secret (HS256)
        .compact();                                     // Convert to string

    return jwtString;
  }

  /**
   * Calculates expiration timestamp for token
   * Adds TOKEN_EXPIRATION_MS milliseconds to issued time
   *
   * @param issuedTime when token was created
   * @return expiration date/time
   */
  private Date calculateTokenExpirationTime(Date issuedTime) {
    long expirationTimestamp = issuedTime.getTime() + EXPIRATION_TIME_MS;
    return new Date(expirationTimestamp);
  }

  // ============= TOKEN VALIDATION =============
  /**
   * Validates JWT token - checks signature and expiration
   * Returns false for any validation failure
   *
   * @param token JWT token to validate
   * @return true if valid and not expired, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      // Parse and extract claims (validates signature)
      Claims tokenPayload = parseTokenToClaims(token);

      // Check expiration time
      Date expiryDate = tokenPayload.getExpiration();
      Date currentTime = new Date();

      if (expiryDate == null) {
        return false; // Token has no expiration date
      }

      // Token is valid if expiration is in the future
      return expiryDate.after(currentTime);

    } catch (JwtException jwtValidationError) {
      return false; // Invalid signature, malformed, or expired
    } catch (IllegalArgumentException illegalArgument) {
      return false; // Null or empty token
    }
  }
}