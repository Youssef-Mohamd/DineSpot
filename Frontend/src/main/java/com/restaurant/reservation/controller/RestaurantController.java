package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.CreateRestaurantRequest;
import com.restaurant.reservation.dto.response.RestaurantResponse;
import com.restaurant.reservation.service.FileStorageService;
import com.restaurant.reservation.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(
            @Valid @RequestBody CreateRestaurantRequest req,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(restaurantService.create(req, auth.getName()));
    }

    /**
     * Upload image for a restaurant
     * POST /api/restaurants/{id}/image
     * Allowed: Restaurant owner OR any ADMIN user
     */
    @PostMapping("/{id}/image")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String userEmail = auth.getName();
        RestaurantResponse response = restaurantService.uploadImage(id, file, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisine) {
        return ResponseEntity.ok(restaurantService.getAll(city, cuisine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateRestaurantRequest req,
            Authentication auth) {
        return ResponseEntity.ok(restaurantService.update(id, req, auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        restaurantService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/image/test")
    public ResponseEntity<String> testUpload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        String path = fileStorageService.saveRestaurantImage(file); // ← مش static
        return ResponseEntity.ok("Uploaded: " + path);
    }
}