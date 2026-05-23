package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.response.*;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final NotificationRepository notificationRepository;

    // ================= USER MANAGEMENT =================

    /**
     * Get all users with reservation statistics
     */
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID with detailed information
     */
    public AdminUserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToAdminUserResponse(user);
    }

    /**
     * Update user role
     */
    public AdminUserResponse updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            User saved = userRepository.save(user);
            return mapToAdminUserResponse(saved);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + newRole + ". Valid roles: CUSTOMER, ADMIN");
        }
    }

    // ================= RESTAURANT MANAGEMENT =================

    /**
     * Get all restaurants (including inactive ones)
     */
    public List<AdminRestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::mapToAdminRestaurantResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get restaurant by ID with detailed admin information
     */
    public AdminRestaurantResponse getRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return mapToAdminRestaurantResponse(restaurant);
    }

    /**
     * Activate or deactivate a restaurant
     */
    public AdminRestaurantResponse toggleRestaurantStatus(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setIsActive(!restaurant.getIsActive());
        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToAdminRestaurantResponse(saved);
    }

    // ================= RESERVATION MANAGEMENT =================

    /**
     * Get all reservations across all restaurants
     */
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Admin override: Cancel any reservation
     */
    public ReservationResponse adminCancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);
        return mapToReservationResponse(saved);
    }

    // ================= SYSTEM STATISTICS =================

    /**
     * Get comprehensive system statistics
     */
    public AdminStatisticsResponse getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalReservations = reservationRepository.count();

        // Count active reservations (confirmed status)
        long activeReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        // Count cancelled reservations
        long cancelledReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        long totalTables = tableRepository.count();
        long totalNotifications = notificationRepository.count();

        // Count unread notifications across all users
        long unreadNotifications = notificationRepository.findAll().stream()
                .filter(n -> !n.getIsRead())
                .count();

        return AdminStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalRestaurants(totalRestaurants)
                .totalReservations(totalReservations)
                .activeReservations(activeReservations)
                .cancelledReservations(cancelledReservations)
                .totalTables(totalTables)
                .totalNotifications(totalNotifications)
                .unreadNotifications(unreadNotifications)
                .build();
    }

    // ================= NOTIFICATION MANAGEMENT =================

    /**
     * Get all notifications in the system
     */
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Send system-wide notification to all users
     */
    public void sendSystemNotification(String message, NotificationType type) {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            Notification notification = Notification.builder()
                    .message(message)
                    .type(type)
                    .user(user)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    // ================= MAPPER METHODS =================

    private AdminUserResponse mapToAdminUserResponse(User user) {
        List<Reservation> userReservations = reservationRepository.findByCustomerId(user.getId());
        long activeReservations = userReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        return AdminUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .totalReservations(userReservations.size())
                .activeReservations((int) activeReservations)
                .build();
    }

    private AdminRestaurantResponse mapToAdminRestaurantResponse(Restaurant restaurant) {
        List<RestaurantTable> tables = tableRepository.findByRestaurantId(restaurant.getId());
        List<Reservation> reservations = reservationRepository.findByRestaurantId(restaurant.getId());
        long activeReservations = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        return AdminRestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .phone(restaurant.getPhone())
                .cuisineType(restaurant.getCuisineType())
                .imageUrl(restaurant.getImageUrl())
                .openingTime(restaurant.getOpeningTime().toString())
                .closingTime(restaurant.getClosingTime().toString())
                .isActive(restaurant.getIsActive())
                .adminName(restaurant.getAdmin().getFullName())
                .adminEmail(restaurant.getAdmin().getEmail())
                .totalTables(tables.size())
                .totalReservations(reservations.size())
                .activeReservations((int) activeReservations)
                .build();
    }

    private ReservationResponse mapToReservationResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .restaurantName(r.getRestaurant().getName())
                .tableNumber(r.getTable().getTableNumber())
                .reservationDate(r.getReservationDate())
                .slotTime(r.getTimeSlot().getSlotTime().toString())
                .guestCount(r.getGuestCount())
                .status(r.getStatus().name())
                .specialRequest(r.getSpecialRequest())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private NotificationResponse mapToNotificationResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
