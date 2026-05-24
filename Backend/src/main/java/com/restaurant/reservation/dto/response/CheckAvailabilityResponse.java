package com.restaurant.reservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponse {
    private boolean isAvailable;
    private Long tableId;
    private Integer tableNumber;
    private String location; // INDOOR, OUTDOOR, TERRACE
    private String message;
}
