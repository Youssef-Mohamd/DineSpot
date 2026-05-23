package com.restaurant.reservation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {
    private Long id;
    private String slotTime;
    private String dayOfWeek;
    private Boolean isActive;
}