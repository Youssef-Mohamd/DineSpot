package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.response.*;
import com.restaurant.reservation.entity.NotificationType;
import com.restaurant.reservation.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ================= USER MANAGEMENT =================

    /**
     * Get all users with statistics
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Get user by ID
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    /**
     * Update user role
     * PUT /api/admin/users/{userId}/role?role=CUSTOMER
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        return ResponseEntity.ok(adminService.updateUserRole(userId, role));
    }

    // ================= RESTAURANT MANAGEMENT =================

    /**
     * Get all restaurants (including inactive)
     * GET /api/admin/restaurants
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<AdminRestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(adminService.getAllRestaurants());
    }

    /**
     * Get restaurant by ID with admin details
     * GET /api/admin/restaurants/{restaurantId}
     */
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<AdminRestaurantResponse> getRestaurantById(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(adminService.getRestaurantById(restaurantId));
    }

    /**
     * Toggle restaurant active status
     * PUT /api/admin/restaurants/{restaurantId}/toggle-status
     */
    @PutMapping("/restaurants/{restaurantId}/toggle-status")
    public ResponseEntity<AdminRestaurantResponse> toggleRestaurantStatus(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(adminService.toggleRestaurantStatus(restaurantId));
    }

    // ================= RESERVATION MANAGEMENT =================

    /**
     * Get all reservations across all restaurants
     * GET /api/admin/reservations
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(adminService.getAllReservations());
    }

    /**
     * Admin override: Cancel any reservation
     * PUT /api/admin/reservations/{reservationId}/cancel
     */
    @PutMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> adminCancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(adminService.adminCancelReservation(reservationId));
    }

    // ================= SYSTEM STATISTICS =================

    /**
     * Get comprehensive system statistics
     * GET /api/admin/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getSystemStatistics() {
        return ResponseEntity.ok(adminService.getSystemStatistics());
    }

    // ================= NOTIFICATION MANAGEMENT =================

    /**
     * Get all notifications in the system
     * GET /api/admin/notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(adminService.getAllNotifications());
    }

    /**
     * Send system-wide notification
     * POST /api/admin/notifications/broadcast
     */
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<Void> sendSystemNotification(
            @RequestParam String message,
            @RequestParam String type) {

        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            adminService.sendSystemNotification(message, notificationType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
