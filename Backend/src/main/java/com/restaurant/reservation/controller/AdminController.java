package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.CreateTableRequest;
import com.restaurant.reservation.dto.request.CreateTimeSlotRequest;
import com.restaurant.reservation.dto.request.UpdateTableRequest;
import com.restaurant.reservation.dto.request.UpdateTimeSlotRequest;
import com.restaurant.reservation.dto.response.*;
import com.restaurant.reservation.entity.NotificationType;
import com.restaurant.reservation.service.AdminService;
import com.restaurant.reservation.service.TableService;
import com.restaurant.reservation.service.TimeSlotService;
import jakarta.validation.Valid;
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
    private final TableService tableService;
    private final TimeSlotService timeSlotService;

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
     * Admin confirm reservation
     * PUT /api/admin/reservations/{reservationId}/confirm
     */
    @PutMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<ReservationResponse> adminConfirmReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(adminService.adminConfirmReservation(reservationId));
    }

    /**
     * Admin override: Cancel any reservation
     * PUT /api/admin/reservations/{reservationId}/cancel
     */
    @PutMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> adminCancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(adminService.adminCancelReservation(reservationId));
    }

    // ================= TABLE MANAGEMENT (per restaurant) =================

    @GetMapping("/restaurants/{restaurantId}/tables")
    public ResponseEntity<List<TableResponse>> getRestaurantTables(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(tableService.getByRestaurant(restaurantId));
    }

    @PostMapping("/restaurants/{restaurantId}/tables")
    public ResponseEntity<TableResponse> createTable(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTableRequest req) {
        return ResponseEntity.status(201).body(tableService.create(restaurantId, req));
    }

    @PutMapping("/restaurants/{restaurantId}/tables/{tableId}")
    public ResponseEntity<TableResponse> updateTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody UpdateTableRequest req) {
        return ResponseEntity.ok(tableService.update(restaurantId, tableId, req));
    }

    @DeleteMapping("/restaurants/{restaurantId}/tables/{tableId}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        tableService.delete(restaurantId, tableId);
        return ResponseEntity.noContent().build();
    }

    // ================= TIME SLOT MANAGEMENT (per restaurant) =================

    @GetMapping("/restaurants/{restaurantId}/slots")
    public ResponseEntity<List<TimeSlotResponse>> getRestaurantSlots(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(timeSlotService.getAllByRestaurant(restaurantId));
    }

    @PostMapping("/restaurants/{restaurantId}/slots")
    public ResponseEntity<TimeSlotResponse> createSlot(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTimeSlotRequest req) {
        return ResponseEntity.status(201).body(timeSlotService.create(restaurantId, req));
    }

    @PutMapping("/restaurants/{restaurantId}/slots/{slotId}")
    public ResponseEntity<TimeSlotResponse> updateSlot(
            @PathVariable Long restaurantId,
            @PathVariable Long slotId,
            @Valid @RequestBody UpdateTimeSlotRequest req) {
        return ResponseEntity.ok(timeSlotService.update(restaurantId, slotId, req));
    }

    @DeleteMapping("/restaurants/{restaurantId}/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable Long restaurantId,
            @PathVariable Long slotId) {
        timeSlotService.delete(restaurantId, slotId);
        return ResponseEntity.noContent().build();
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
