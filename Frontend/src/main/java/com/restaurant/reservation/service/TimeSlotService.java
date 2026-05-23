package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.CreateTimeSlotRequest;
import com.restaurant.reservation.dto.response.TimeSlotResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final RestaurantRepository restaurantRepository;

    //  CREATE TIMESLOT
    public TimeSlotResponse create(Long restaurantId, CreateTimeSlotRequest req) {

        // Validate Restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        //  Safe parsing for time
        LocalTime slotTime;
        try {
            slotTime = LocalTime.parse(req.getSlotTime());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid time format. Use HH:mm (e.g., 18:30)");
        }

        //  Safe enum conversion
        DayOfWeek dayOfWeek = null;
        if (req.getDayOfWeek() != null) {
            try {
                dayOfWeek = DayOfWeek.valueOf(req.getDayOfWeek().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid dayOfWeek value");
            }
        }

        //  Prevent duplicate slots
        boolean exists = timeSlotRepository
                .existsByRestaurantIdAndSlotTimeAndDayOfWeek(
                        restaurantId, slotTime, dayOfWeek);

        if (exists) {
            throw new RuntimeException("Time slot already exists for this restaurant");
        }

        //  Create entity
        TimeSlot slot = TimeSlot.builder()
                .slotTime(slotTime)
                .dayOfWeek(dayOfWeek)
                .isActive(true)
                .restaurant(restaurant)
                .build();


        TimeSlot saved = timeSlotRepository.save(slot);

        return mapToResponse(saved);
    }

    //  GET
    public List<TimeSlotResponse> getByRestaurant(Long restaurantId) {
        return timeSlotRepository.findByRestaurantIdAndIsActiveTrue(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // === MAPPER ===
    private TimeSlotResponse mapToResponse(TimeSlot s) {
        return TimeSlotResponse.builder()
                .id(s.getId())
                .slotTime(s.getSlotTime().toString())
                .dayOfWeek(s.getDayOfWeek() != null ? s.getDayOfWeek().name() : null)
                .isActive(s.getIsActive())
                .build();
    }

    public void delete(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        timeSlotRepository.delete(slot);
    }
}