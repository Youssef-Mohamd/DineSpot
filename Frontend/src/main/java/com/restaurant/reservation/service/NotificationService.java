package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.response.NotificationResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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
}