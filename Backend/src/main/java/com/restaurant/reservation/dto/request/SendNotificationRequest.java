package com.restaurant.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    // Notification message
    private String message;
    
    // User IDs to send to
    private List<Long> userIds;
    
    // OR: Send to all users
    private Boolean sendToAll;
    
    // Filter by user role (CUSTOMER, RESTAURANT_OWNER)
    private String role;
    
    // Filter by restaurant (send to all customers who made reservations)
    private Long restaurantId;
    
    // Notification type
    private String type; // BOOKING_CONFIRMED, BOOKING_CANCELLED, SYSTEM_MAINTENANCE, PROMOTION, GENERAL
    
    // Additional data (optional)
    private String title;
    private String actionUrl;
    private Boolean urgent;
}