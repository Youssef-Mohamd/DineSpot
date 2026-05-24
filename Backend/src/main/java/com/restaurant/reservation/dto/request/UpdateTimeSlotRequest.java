package com.restaurant.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTimeSlotRequest {

    private String slotTime;
    private String dayOfWeek;
    private Boolean isActive;
}
