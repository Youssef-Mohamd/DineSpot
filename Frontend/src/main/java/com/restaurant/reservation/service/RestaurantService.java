package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.CreateRestaurantRequest;
import com.restaurant.reservation.dto.response.RestaurantResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public RestaurantResponse create(CreateRestaurantRequest req, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .description(req.getDescription())
                .address(req.getAddress())
                .city(req.getCity())
                .phone(req.getPhone())
                .cuisineType(req.getCuisineType())
                .imageUrl(req.getImageUrl())
                .openingTime(LocalTime.parse(req.getOpeningTime()))
                .closingTime(LocalTime.parse(req.getClosingTime()))
                .isActive(true)
                .admin(admin)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToResponse(saved);
    }

    /**
     * Upload image for restaurant
     * Allowed: Restaurant owner OR any ADMIN user
     */
    public RestaurantResponse uploadImage(Long restaurantId, MultipartFile imageFile, String userEmail) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Fetch user from database to check their actual role
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is authorized:
        // 1. User is the owner of the restaurant, OR
        // 2. User has ADMIN role
        boolean isOwner = restaurant.getAdmin().getEmail().equals(userEmail);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You can only upload images for your own restaurants or you must be an admin");
        }

        // Delete old image if exists
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            fileStorageService.deleteRestaurantImage(restaurant.getImageUrl());
        }

        // Save new image
        String imagePath = fileStorageService.saveRestaurantImage(imageFile);
        restaurant.setImageUrl(imagePath);

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    public List<RestaurantResponse> getAll(String city, String cuisine) {
        List<Restaurant> restaurants;

        if (city != null && cuisine != null) {
            restaurants = restaurantRepository.findByCityAndCuisineType(city, cuisine);
        } else if (city != null) {
            restaurants = restaurantRepository.findByCity(city);
        } else if (cuisine != null) {
            restaurants = restaurantRepository.findByCuisineType(cuisine);
        } else {
            restaurants = restaurantRepository.findByIsActiveTrue();
        }

        return restaurants.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return mapToResponse(restaurant);
    }

    public RestaurantResponse update(Long restaurantId, CreateRestaurantRequest req, String adminEmail) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Check if user is the admin of this restaurant
        if (!restaurant.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("You can only update your own restaurants");
        }

        restaurant.setName(req.getName());
        restaurant.setDescription(req.getDescription());
        restaurant.setAddress(req.getAddress());
        restaurant.setCity(req.getCity());
        restaurant.setPhone(req.getPhone());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setOpeningTime(LocalTime.parse(req.getOpeningTime()));
        restaurant.setClosingTime(LocalTime.parse(req.getClosingTime()));

        // Only update imageUrl if provided (don't overwrite with null)
        if (req.getImageUrl() != null && !req.getImageUrl().isEmpty()) {
            restaurant.setImageUrl(req.getImageUrl());
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    public void delete(Long restaurantId, String adminEmail) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Check if user is the admin of this restaurant
        if (!restaurant.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("You can only delete your own restaurants");
        }

        // Delete associated image file if exists
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            fileStorageService.deleteRestaurantImage(restaurant.getImageUrl());
        }

        restaurantRepository.delete(restaurant);
    }

    private RestaurantResponse mapToResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .cuisineType(r.getCuisineType())
                .imageUrl(r.getImageUrl())
                .openingTime(r.getOpeningTime().toString())
                .closingTime(r.getClosingTime().toString())
                .isActive(r.getIsActive())
                .build();
    }
}