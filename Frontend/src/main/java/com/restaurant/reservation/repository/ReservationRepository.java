package com.restaurant.reservation.repository;

import com.restaurant.reservation.entity.Reservation;
import com.restaurant.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByRestaurantId(Long restaurantId);
    List<Reservation> findByRestaurantIdAndReservationDate(Long restaurantId, LocalDate date);
    List<Reservation> findByRestaurantIdAndStatus(Long restaurantId, ReservationStatus status);
    boolean existsByTableIdAndReservationDateAndTimeSlotIdAndStatusNot(
            Long tableId,
            LocalDate date,
            Long timeSlotId,
            ReservationStatus status
    );
}