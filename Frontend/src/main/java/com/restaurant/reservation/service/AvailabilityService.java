package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.AvailabilityRequest;
import com.restaurant.reservation.dto.response.AvailableSlotResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import com.restaurant.reservation.strategyPattern.TableAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final TimeSlotRepository timeSlotRepository;
    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final TableAssignmentStrategy strategy;

    // =========== MAIN METHOD ===========
    public List<AvailableSlotResponse> getAvailableSlots(AvailabilityRequest request) {

        List<TimeSlot> slots =
                timeSlotRepository.findByRestaurantIdAndIsActiveTrue(
                        request.getRestaurantId()
                );

        return slots.stream()
                .filter(slot -> hasAvailableTable(
                        request.getRestaurantId(),
                        request.getDate(),
                        slot.getId(),
                        request.getGuests()
                ))
                .map(slot -> AvailableSlotResponse.builder()
                        .id(slot.getId())
                        .slotTime(slot.getSlotTime().toString())
                        .build())
                .collect(Collectors.toList());
    }

    // =========== CHECK AVAILABILITY ===========
    public boolean hasAvailableTable(Long restaurantId,
                                     java.time.LocalDate date,
                                     Long slotId,
                                     int guests) {

        List<RestaurantTable> tables =
                tableRepository.findByRestaurantIdAndCapacityGreaterThanEqual(
                        restaurantId, guests);

        if (tables.isEmpty()) {
            return false;
        }

        List<RestaurantTable> availableTables = tables.stream()
                .filter(table -> !reservationRepository
                        .existsByTableIdAndReservationDateAndTimeSlotIdAndStatusNot(
                                table.getId(),
                                date,
                                slotId,
                                ReservationStatus.CANCELLED))
                .toList();

        if (availableTables.isEmpty()) {
            return false;
        }

        try {
            strategy.assign(availableTables, guests);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}