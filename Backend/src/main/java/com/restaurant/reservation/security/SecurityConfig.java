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

    // Inject JWT filter
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF (not needed for stateless APIs)
                .csrf(csrf -> csrf.disable())

                // Make session stateless (because we use JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
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

                )

                // Add JWT filter before default authentication filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ================= CORS CONFIG =================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // سماح بتبادل ملفات تعريف الارتباط والـ Credentials
        config.setAllowCredentials(true);

        // إضافة الـ IP بتاعك والـ Pattern للشبكة المحلية هنا
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://192.168.1.11:4200",
                "http://192.168.1.11:8081",
                "http://192.168.1.*"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // السماح بجميع الـ Headers القياسية
        config.setAllowedHeaders(List.of("*"));

        // إظهار والسماح بالـ Headers المخصصة اللي الأنجولار بيبعتها صراحة
        config.setExposedHeaders(List.of("token"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ================= PASSWORD ENCODER =================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ================= AUTH MANAGER =================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}