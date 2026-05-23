package com.restaurant.reservation.ObserverPattern;

import com.restaurant.reservation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleReservationEvent(ReservationEvent event) {
        if (event.getType().equals("CREATED")) {
            notificationService.sendBookingConfirmation(event.getReservation());
        } else if (event.getType().equals("CANCELLED")) {
            notificationService.sendCancellationNotice(event.getReservation());
        }
    }
}