package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.CreateTableRequest;
import com.restaurant.reservation.dto.response.TableResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {


    private final RestaurantTableRepository tableRepository;

    // Repository to interact with Restaurant entity in DB
    private final RestaurantRepository restaurantRepository;

    // CREATE TABLE
    public TableResponse create(Long restaurantId, CreateTableRequest req) {

        // Fetch restaurant by ID or throw exception if not found
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Build new RestaurantTable object using Builder pattern
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(req.getTableNumber())
                .capacity(req.getCapacity())
                .location(Location.valueOf(req.getLocation().toUpperCase()))
                // Convert String location to Enum (case insensitive)
                .isAvailable(true) // New tables are available by default
                .restaurant(restaurant) // Link table to its restaurant
                .build();


        RestaurantTable saved = tableRepository.save(table);

        // Convert entity to response DTO and return
        return mapToResponse(saved);
    }

    //  GET TABLES BY RESTAURANT
    public List<TableResponse> getByRestaurant(Long restaurantId) {

        // Fetch tables by restaurant ID
        return tableRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse) // Convert each entity to DTO
                .collect(Collectors.toList()); // Return as List
    }

    //  MAPPING METHOD
    // Converts RestaurantTable entity → TableResponse DTO
    private TableResponse mapToResponse(RestaurantTable t) {
        return TableResponse.builder()
                .id(t.getId())
                .tableNumber(t.getTableNumber())
                .capacity(t.getCapacity())
                .location(t.getLocation().name())
                .isAvailable(t.getIsAvailable()) // Availability status
                .build();
    }

    public void delete(Long tableId) {
        tableRepository.deleteById(tableId);
    }
}