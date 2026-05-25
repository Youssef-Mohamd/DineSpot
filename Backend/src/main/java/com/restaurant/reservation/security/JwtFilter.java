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

  private final JwtUtil jwtUtil;

  private String normalizeRole(String roleValue) {
    if (roleValue == null || roleValue.isBlank()) {
      return "CUSTOMER";
    }
    if (roleValue.startsWith("ROLE_")) {
      return roleValue.substring(5);
    }
    return roleValue.toUpperCase();
  }

  private String resolveToken(HttpServletRequest httpRequest) {
    String bearerHeader = httpRequest.getHeader("Authorization");
    if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
      return bearerHeader.substring(7).trim();
    }

    String customToken = httpRequest.getHeader("token");
    if (customToken != null && !customToken.isBlank()) {
      return customToken.trim();
    }

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

    if (jwtToken != null && jwtUtil.isTokenValid(jwtToken)) {
      String userEmail = jwtUtil.extractEmail(jwtToken);
      String userRole = normalizeRole(jwtUtil.extractRole(jwtToken));

      log.debug("Valid token for email: {} with role: {}", userEmail, userRole);

      var authentication = new UsernamePasswordAuthenticationToken(
          userEmail,
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + userRole))
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
    } else if (jwtToken != null) {
      log.warn("Invalid or expired JWT token received");
    }

    filterChain.doFilter(request, response);
  }

}
