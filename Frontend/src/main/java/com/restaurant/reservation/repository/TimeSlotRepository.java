package com.restaurant.reservation.repository;

import com.restaurant.reservation.entity.DayOfWeek;
import com.restaurant.reservation.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByRestaurantId(Long restaurantId);
    List<TimeSlot> findByRestaurantIdAndIsActiveTrue(Long restaurantId);

    //  Prevent duplicate (same time + same day + same restaurant)
    boolean existsByRestaurantIdAndSlotTimeAndDayOfWeek(
            Long restaurantId,
            LocalTime slotTime,
            DayOfWeek dayOfWeek
    );
}