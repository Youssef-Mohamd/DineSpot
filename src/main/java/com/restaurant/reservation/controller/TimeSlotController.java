package com.restaurant.reservation.controller;



import com.restaurant.reservation.dto.request.CreateTimeSlotRequest;
import com.restaurant.reservation.dto.request.UpdateTimeSlotRequest;
import com.restaurant.reservation.dto.response.TimeSlotResponse;
import com.restaurant.reservation.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping("/api/restaurants/{restaurantId}/slots")
    public ResponseEntity<TimeSlotResponse> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTimeSlotRequest req) {
        return ResponseEntity.status(201)
                .body(timeSlotService.create(restaurantId, req));
    }

    @GetMapping("/api/restaurants/{restaurantId}/slots")
    public ResponseEntity<List<TimeSlotResponse>> getAll(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(timeSlotService.getByRestaurant(restaurantId));
    }

    /** All slots including inactive — requires ADMIN (used by admin dashboard). */
    @GetMapping("/api/restaurants/{restaurantId}/slots/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeSlotResponse>> getAllIncludingInactive(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(timeSlotService.getAllByRestaurant(restaurantId));
    }

    @PutMapping("/api/restaurants/{restaurantId}/slots/{slotId}")
    public ResponseEntity<TimeSlotResponse> update(
            @PathVariable Long restaurantId,
            @PathVariable Long slotId,
            @Valid @RequestBody UpdateTimeSlotRequest req) {
        return ResponseEntity.ok(timeSlotService.update(restaurantId, slotId, req));
    }

    @DeleteMapping("/api/restaurants/{restaurantId}/slots/{slotId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long slotId) {
        timeSlotService.delete(restaurantId, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/timeslots/available")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @RequestParam Long restaurantId,
            @RequestParam String date) {
        return ResponseEntity.ok(timeSlotService.getAvailableSlots(restaurantId, date));
    }
}