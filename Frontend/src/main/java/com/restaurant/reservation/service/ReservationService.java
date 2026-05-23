package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.CreateReservationRequest;
import com.restaurant.reservation.dto.response.ReservationResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.ObserverPattern.ReservationEvent;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    // Repositories for database operations
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    // Service for checking availability
    private final AvailabilityService availabilityService;

    // Strategy-based table assignment (better to use TableAssignmentService in future)
    private final TableAssignmentService tableAssignmentService;

    // Event publisher (Observer Pattern)
    private final ApplicationEventPublisher eventPublisher;

    // ================= CREATE RESERVATION =================
    @Transactional //  Ensures atomic operation (prevents race conditions)
    public ReservationResponse create(CreateReservationRequest req, String customerEmail) {

        //  Fetch user (customer)
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Fetch restaurant
        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        //  Fetch time slot
        TimeSlot timeSlot = timeSlotRepository.findById(req.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        //  Parse reservation date
        LocalDate date = LocalDate.parse(req.getReservationDate());

        //  Availability check before proceeding
        boolean isAvailable = availabilityService.hasAvailableTable(
                req.getRestaurantId(),
                date,
                req.getTimeSlotId(),
                req.getGuestCount()
        );

        if (!isAvailable) {
            throw new RuntimeException("No available tables for this slot");
        }

        //  Fetch all tables that can fit the guest count
        List<RestaurantTable> freeTables = tableRepository
                .findByRestaurantIdAndCapacityGreaterThanEqual(
                        req.getRestaurantId(),
                        req.getGuestCount()
                )
                .stream()

                // Filter out reserved tables for the same date and slot
                .filter(table -> !reservationRepository
                        .existsByTableIdAndReservationDateAndTimeSlotIdAndStatusNot(
                                table.getId(),
                                date,
                                req.getTimeSlotId(),
                                ReservationStatus.CANCELLED
                        ))
                .collect(Collectors.toList());

        //  Use Strategy Pattern to assign the best table
        RestaurantTable assignedTable = tableAssignmentService
                .assignTable("bestFit", freeTables, req.getGuestCount());

        //  Build reservation entity
        Reservation reservation = Reservation.builder()
                .customer(customer)
                .restaurant(restaurant)
                .table(assignedTable)
                .timeSlot(timeSlot)
                .reservationDate(date)
                .guestCount(req.getGuestCount())
                .specialRequest(req.getSpecialRequest())
                .build();

        //  Save reservation
        Reservation saved = reservationRepository.save(reservation);

        // Publish event (Observer Pattern)
        eventPublisher.publishEvent(new ReservationEvent(this, saved, "CREATED"));

        return mapToResponse(saved);
    }

    // ================= CONFIRM RESERVATION =================
    @Transactional
    public ReservationResponse confirm(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        return mapToResponse(saved);
    }

    // ================= CANCEL RESERVATION =================
    @Transactional
    public void cancel(Long reservationId, String userEmail) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        reservationRepository.save(reservation);

        // Publish cancellation event
        eventPublisher.publishEvent(new ReservationEvent(this, reservation, "CANCELLED"));
    }

    // ================= GET USER RESERVATIONS =================
    public List<ReservationResponse> getMyReservations(String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reservationRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= GET RESTAURANT RESERVATIONS =================
    public List<ReservationResponse> getRestaurantReservations(Long restaurantId) {

        return reservationRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= MAPPER =================
    private ReservationResponse mapToResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .restaurantName(r.getRestaurant().getName())
                .tableNumber(r.getTable().getTableNumber())
                .reservationDate(r.getReservationDate())
                .slotTime(r.getTimeSlot().getSlotTime().toString())
                .guestCount(r.getGuestCount())
                .status(r.getStatus().name())
                .specialRequest(r.getSpecialRequest())
                .createdAt(r.getCreatedAt())
                .build();
    }

    public List<ReservationResponse> getTodayReservations(Long restaurantId) {
        return reservationRepository
                .findByRestaurantIdAndReservationDate(restaurantId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}