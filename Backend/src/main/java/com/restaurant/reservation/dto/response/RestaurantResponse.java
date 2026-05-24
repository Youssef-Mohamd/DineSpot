package com.restaurant.reservation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {
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
}