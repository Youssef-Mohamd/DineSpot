package com.restaurant.reservation.security;

import com.restaurant.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Load user from database using email
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Fetch user from DB
        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        // Convert to Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),           // username
                user.getPassword(),        // encoded password
                List.of(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ) // user roles
        );
    }
}
