package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.SendNotificationRequest;
import com.restaurant.reservation.dto.response.NotificationResponse;
import com.restaurant.reservation.dto.response.NotificationSendResponse;
import com.restaurant.reservation.service.NotificationService;
import com.restaurant.reservation.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication auth) {
        Long userId = authService.getMe(auth.getName()).getId();
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * ADMIN ENDPOINT: Send notification to specific users
     * POST /api/notifications/send-to-users
     */
    @PostMapping("/admin/send-to-users")
    public ResponseEntity<NotificationSendResponse> sendToSpecificUsers(
            @RequestBody SendNotificationRequest request,
            Authentication auth) {
        
        // Verify user is ADMIN
        authService.validateAdminRole(auth.getName());
        
        NotificationSendResponse response = notificationService.sendNotificationToUsers(
                request.getUserIds(),
                request.getMessage(),
                request.getType() != null ? request.getType() : "GENERAL",
                request.getTitle(),
                request.getActionUrl()
        );
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * ADMIN ENDPOINT: Send notification to users by role
     * POST /api/notifications/send-by-role
     */
    @PostMapping("/admin/send-by-role")
    public ResponseEntity<NotificationSendResponse> sendByRole(
            @RequestBody SendNotificationRequest request,
            Authentication auth) {
        
        authService.validateAdminRole(auth.getName());
        
        NotificationSendResponse response = notificationService.sendNotificationByRole(
                request.getRole(),
                request.getMessage(),
                request.getType() != null ? request.getType() : "GENERAL",
                request.getTitle(),
                request.getActionUrl()
        );
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * ADMIN ENDPOINT: Send notification to restaurant customers
     * POST /api/notifications/send-by-restaurant
     */
    @PostMapping("/admin/send-by-restaurant")
    public ResponseEntity<NotificationSendResponse> sendByRestaurant(
            @RequestBody SendNotificationRequest request,
            Authentication auth) {
        
        authService.validateAdminRole(auth.getName());
        
        NotificationSendResponse response = notificationService.sendNotificationByRestaurant(
                request.getRestaurantId(),
                request.getMessage(),
                request.getType() != null ? request.getType() : "GENERAL",
                request.getTitle(),
                request.getActionUrl()
        );
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * ADMIN ENDPOINT: Send notification to all users
     * POST /api/notifications/admin/send-all
     */
    @PostMapping("/admin/send-all")
    public ResponseEntity<NotificationSendResponse> sendToAllUsers(
            @RequestBody SendNotificationRequest request,
            Authentication auth) {
        
        authService.validateAdminRole(auth.getName());
        
        NotificationSendResponse response = notificationService.sendNotificationToAllUsers(
                request.getMessage(),
                request.getType() != null ? request.getType() : "GENERAL",
                request.getTitle(),
                request.getActionUrl()
        );
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

