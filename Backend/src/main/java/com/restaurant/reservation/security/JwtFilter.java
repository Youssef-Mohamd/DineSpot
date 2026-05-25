package com.restaurant.reservation.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  // Constants for token extraction
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String LEGACY_TOKEN_HEADER = "token";
  private static final String ROLE_PREFIX = "ROLE_";
  private static final String DEFAULT_ROLE = "CUSTOMER";
  private static final int BEARER_TOKEN_START_INDEX = 7;

  private final JwtUtil jwtUtil;

  // Helper method to extract and normalize roles from token
  // Returns CUSTOMER if role is null/blank, removes ROLE_ prefix if present, uppercase otherwise
  private String normalizeRole(String roleValue) {
    if (roleValue == null || roleValue.isBlank()) {
      log.trace("Role is null or blank, defaulting to {}", DEFAULT_ROLE);
      return DEFAULT_ROLE;
    }
    if (roleValue.startsWith(ROLE_PREFIX)) {
      return roleValue.substring(ROLE_PREFIX.length());
    }
    return roleValue.toUpperCase();
  }

  // Extracts JWT token from request headers
  // First checks Authorization header with Bearer prefix (RFC 6750)
  // Falls back to legacy custom token header if Bearer token not found
  private String resolveToken(HttpServletRequest httpRequest) {
    // Check standard Authorization header first (RFC 6750: Bearer <token>)
    String bearerHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
    if (bearerHeader != null && bearerHeader.startsWith(BEARER_PREFIX)) {
      String extractedToken = bearerHeader.substring(BEARER_TOKEN_START_INDEX).trim();
      log.trace("Token extracted from Authorization header");
      return extractedToken;
    }

    // Fallback to custom token header for backward compatibility
    String customToken = httpRequest.getHeader(LEGACY_TOKEN_HEADER);
    if (customToken != null && !customToken.isBlank()) {
      log.trace("Token extracted from legacy custom header");
      return customToken.trim();
    }

    log.trace("No token found in request headers");
    return null;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    String jwtToken = resolveToken(request);

    log.debug("JWT Filter - Method: {} URI: {} Token: {}",
        request.getMethod(), request.getRequestURI(), jwtToken != null);

    // Process token if present and valid
    if (jwtToken != null && jwtUtil.isTokenValid(jwtToken)) {
      try {
        String userEmail = jwtUtil.extractEmail(jwtToken);
        String userRole = normalizeRole(jwtUtil.extractRole(jwtToken));

        log.debug("Valid token for email: {} with role: {}", userEmail, userRole);

        // Create authentication token and set in security context
        var authentication = new UsernamePasswordAuthenticationToken(
            userEmail,
            null,
            List.of(new SimpleGrantedAuthority(ROLE_PREFIX + userRole))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.trace("Authentication set in SecurityContext for user: {}", userEmail);
      } catch (Exception e) {
        log.error("Error processing valid JWT token", e);
      }
    } else if (jwtToken != null) {
      log.warn("Invalid or expired JWT token received");
    }

    // Continue filter chain with or without authentication
    filterChain.doFilter(request, response);
  }

}
