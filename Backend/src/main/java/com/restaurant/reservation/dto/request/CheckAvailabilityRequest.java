package com.restaurant.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityRequest {
    private Long restaurantId;
    private String reservationDate;
    private Long timeSlotId;
    private Integer guestCount;
}
