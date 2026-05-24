package com.restaurant.reservation.ObserverPattern;

import com.restaurant.reservation.entity.Reservation;
import org.springframework.context.ApplicationEvent;

public class ReservationEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final String type;

    public ReservationEvent(Object source, Reservation reservation, String type) {
        super(source);
        this.reservation = reservation;
        this.type = type;
    }

    public Reservation getReservation() { return reservation; }
    public String getType() { return type; }
}