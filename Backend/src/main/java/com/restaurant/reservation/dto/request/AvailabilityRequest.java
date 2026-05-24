package com.restaurant.reservation.dto.request;


import lombok.Data;

import java.time.LocalDate;
@Data

public class AvailabilityRequest {

    private Long restaurantId;
    private LocalDate date;
    private int guests;


}