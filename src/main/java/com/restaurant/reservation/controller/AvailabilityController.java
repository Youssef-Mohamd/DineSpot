package com.restaurant.reservation.controller;
import com.restaurant.reservation.dto.request.AvailabilityRequest;
import com.restaurant.reservation.dto.response.AvailableSlotResponse;
import com.restaurant.reservation.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @RequestBody AvailabilityRequest request) {

        return ResponseEntity.ok(
                availabilityService.getAvailableSlots(request)
        );
    }
}


