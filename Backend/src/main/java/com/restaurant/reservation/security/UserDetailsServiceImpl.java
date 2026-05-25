package com.restaurant.reservation.security;

import com.restaurant.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementation of Spring Security's UserDetailsService
 * Loads user authentication details from database for login and authorization
 * Converts application User entities to Spring Security UserDetails objects
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  // Constants for user details configuration
  private static final String ROLE_PREFIX = "ROLE_";
  private static final String USER_NOT_FOUND_MSG = "User not found with email: ";

  private final UserRepository userRepository;

  /**
   * Loads user authentication details from database by email
   * Called by Spring Security during login and authentication
   * Converts application User to Spring Security UserDetails
   *
   * @param emailAddress the user's email (used as username in authentication)
   * @return UserDetails object with credentials and authorities
   * @throws UsernameNotFoundException if user with email not found in database
   */
  @Override
  public UserDetails loadUserByUsername(String emailAddress)
      throws UsernameNotFoundException {

    log.debug("Loading user details for email: {}", emailAddress);

    // Retrieve user from database by email address
    var appUser = userRepository.findByEmail(emailAddress)
        .orElseThrow(() -> {
          log.warn("User not found with email: {}", emailAddress);
          return new UsernameNotFoundException(USER_NOT_FOUND_MSG + emailAddress);
        });

    // Convert application user role to Spring Security granted authority
    var userAuthorities = List.of(
        new SimpleGrantedAuthority(ROLE_PREFIX + appUser.getRole().name())
    );

    log.debug("User details loaded successfully for: {} with role: {}",
        emailAddress, appUser.getRole().name());

    // Build Spring Security User with email, password, and authorities
    return User.builder()
        .username(appUser.getEmail())
        .password(appUser.getPassword())
        .authorities(userAuthorities)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}
