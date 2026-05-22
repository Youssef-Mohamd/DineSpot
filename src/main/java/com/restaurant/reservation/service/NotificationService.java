package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.response.NotificationResponse;
import com.restaurant.reservation.dto.response.NotificationSendResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public void sendBookingConfirmation(Reservation reservation) {
        Notification notification = Notification.builder()
                .message("Your reservation at " + reservation.getRestaurant().getName()
                        + " on " + reservation.getReservationDate()
                        + " at " + reservation.getTimeSlot().getSlotTime()
                        + " has been confirmed.")
                .type(NotificationType.BOOKING_CONFIRMED)
                .user(reservation.getCustomer())
                .build();

        notificationRepository.save(notification);
    }

    public void sendCancellationNotice(Reservation reservation) {
        Notification notification = Notification.builder()
                .message("Your reservation at " + reservation.getRestaurant().getName()
                        + " on " + reservation.getReservationDate()
                        + " has been cancelled.")
                .type(NotificationType.BOOKING_CANCELLED)
                .user(reservation.getCustomer())
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    /**
     * Send notification to specific users by ID
     */
    public NotificationSendResponse sendNotificationToUsers(List<Long> userIds, String message, 
                                                           String type, String title, String actionUrl) {
        List<String> failedUserIds = new ArrayList<>();
        int successCount = 0;
        
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
        
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                
                Notification notification = Notification.builder()
                        .message(message)
                        .type(notificationType)
                        .title(title)
                        .actionUrl(actionUrl)
                        .user(user)
                        .build();
                
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                failedUserIds.add(userId.toString());
            }
        }
        
        return buildResponse(successCount, userIds.size(), failedUserIds, "SPECIFIC_USERS");
    }

    /**
     * Send notification to all users with a specific role
     */
    public NotificationSendResponse sendNotificationByRole(String role, String message, 
                                                          String type, String title, String actionUrl) {
        List<User> users = userRepository.findByRole(role);
        List<String> failedUserIds = new ArrayList<>();
        int successCount = 0;
        
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
        
        for (User user : users) {
            try {
                Notification notification = Notification.builder()
                        .message(message)
                        .type(notificationType)
                        .title(title)
                        .actionUrl(actionUrl)
                        .user(user)
                        .build();
                
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                failedUserIds.add(user.getId().toString());
            }
        }
        
        return buildResponse(successCount, users.size(), failedUserIds, "BY_ROLE");
    }

    /**
     * Send notification to all customers who made reservations at a specific restaurant
     */
    public NotificationSendResponse sendNotificationByRestaurant(Long restaurantId, String message, 
                                                                String type, String title, String actionUrl) {
        List<Reservation> reservations = reservationRepository.findByRestaurantId(restaurantId);
        
        // Get unique customer IDs
        List<User> uniqueCustomers = reservations.stream()
                .map(Reservation::getCustomer)
                .distinct()
                .collect(Collectors.toList());
        
        List<String> failedUserIds = new ArrayList<>();
        int successCount = 0;
        
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
        
        for (User customer : uniqueCustomers) {
            try {
                Notification notification = Notification.builder()
                        .message(message)
                        .type(notificationType)
                        .title(title)
                        .actionUrl(actionUrl)
                        .user(customer)
                        .build();
                
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                failedUserIds.add(customer.getId().toString());
            }
        }
        
        return buildResponse(successCount, uniqueCustomers.size(), failedUserIds, "BY_RESTAURANT");
    }

    /**
     * Send notification to all users in the system
     */
    public NotificationSendResponse sendNotificationToAllUsers(String message, String type, 
                                                               String title, String actionUrl) {
        List<User> allUsers = userRepository.findAll();
        List<String> failedUserIds = new ArrayList<>();
        int successCount = 0;
        
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
        
        for (User user : allUsers) {
            try {
                Notification notification = Notification.builder()
                        .message(message)
                        .type(notificationType)
                        .title(title)
                        .actionUrl(actionUrl)
                        .user(user)
                        .build();
                
                notificationRepository.save(notification);
                successCount++;
            } catch (Exception e) {
                failedUserIds.add(user.getId().toString());
            }
        }
        
        return buildResponse(successCount, allUsers.size(), failedUserIds, "ALL_USERS");
    }

    /**
     * Helper method to build NotificationSendResponse
     */
    private NotificationSendResponse buildResponse(int successCount, int totalCount, 
                                                   List<String> failedUserIds, String sendType) {
        int failureCount = totalCount - successCount;
        String status = failureCount == 0 ? "SUCCESS" : (successCount > 0 ? "PARTIAL_SUCCESS" : "FAILED");
        
        return NotificationSendResponse.builder()
                .message("Notifications sent successfully")
                .totalSent(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .failedUserIds(failedUserIds)
                .status(status)
                .timestamp(System.currentTimeMillis())
                .sendType(sendType)
                .build();
    }
}
