package com.restaurant.reservation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/restaurants/*/image/test").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").authenticated()
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/uploads/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/restaurants/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/restaurants/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/**").hasRole("ADMIN")
        .requestMatchers("/api/availability/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    );

    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();

    corsConfig.setAllowCredentials(true);

    List<String> allowedOrigins = List.of(
        "http://localhost:4200",
        "http://127.0.0.1:4200",
        "http://192.168.1.11:4200",
        "http://192.168.1.11:8081",
        "http://192.168.1.*"
    );
    corsConfig.setAllowedOriginPatterns(allowedOrigins);

    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfig.setAllowedHeaders(List.of("*"));
    corsConfig.setExposedHeaders(List.of("token"));

    UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
    corsSource.registerCorsConfiguration("/**", corsConfig);

    return corsSource;
  }
}