package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.CreateReservationRequest;
import com.restaurant.reservation.dto.response.ReservationResponse;
import com.restaurant.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest req,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(reservationService.create(req, auth.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Authentication auth) {
        return ResponseEntity.ok(reservationService.getMyReservations(auth.getName()));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication auth) {
        reservationService.cancel(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReservationResponse>> getByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(reservationService.getRestaurantReservations(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/today")
    public ResponseEntity<List<ReservationResponse>> getToday(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(reservationService.getTodayReservations(restaurantId));
    }
}
