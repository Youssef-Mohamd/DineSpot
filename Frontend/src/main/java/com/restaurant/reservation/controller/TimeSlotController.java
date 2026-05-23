package com.restaurant.reservation.controller;



import com.restaurant.reservation.dto.request.CreateTimeSlotRequest;
import com.restaurant.reservation.dto.response.TimeSlotResponse;
import com.restaurant.reservation.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    public ResponseEntity<TimeSlotResponse> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTimeSlotRequest req) {
        return ResponseEntity.status(201)
                .body(timeSlotService.create(restaurantId, req));
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getAll(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(timeSlotService.getByRestaurant(restaurantId));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long slotId) {
        timeSlotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }
}