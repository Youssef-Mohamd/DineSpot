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

    // Utility class for JWT operations
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        log.debug("=== JWT Filter === {} {} | Token present: {}",
                request.getMethod(), request.getRequestURI(), token != null);

        if (token != null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            String role = normalizeRole(jwtUtil.extractRole(token));

            log.debug("=== JWT Filter === Token valid | email={} | role={}",
                    email, role);

            var auth = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        } else if (token != null) {
            log.warn("=== JWT Filter === Token is INVALID or EXPIRED");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        String legacyToken = request.getHeader("token");
        if (legacyToken != null && !legacyToken.isBlank()) {
            return legacyToken.trim();
        }

        return null;
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CUSTOMER";
        }
        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }
        return role.toUpperCase();
    }

}
