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

        // Get Authorization header from request
        String authHeader = request.getHeader("Authorization");

        log.debug("=== JWT Filter === {} {} | Auth header present: {}",
                request.getMethod(), request.getRequestURI(), authHeader != null);

        // Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extract token from header
            String token = authHeader.substring(7);

            // Validate token
            if (jwtUtil.isTokenValid(token)) {

                // Extract user data from token
                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);

                log.debug("=== JWT Filter === Token valid | email={} | role={} | authority=ROLE_{}",
                        email, role, role);

                // Create authentication object with user info and role
                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Store authentication in security context
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("=== JWT Filter === Token is INVALID or EXPIRED");
            }
        } else {
            log.debug("=== JWT Filter === No Bearer token found in request");
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
