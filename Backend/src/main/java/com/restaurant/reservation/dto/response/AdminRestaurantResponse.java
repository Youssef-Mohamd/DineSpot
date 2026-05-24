package com.restaurant.reservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String phone;
    private String cuisineType;
    private String imageUrl;
    private String openingTime;
    private String closingTime;
    private Boolean isActive;
    private String adminName;
    private String adminEmail;
    private Integer totalTables;
    private Integer totalReservations;
    private Integer activeReservations;
}
