package com.restaurant.reservation.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeSlotRequest {

    @NotBlank(message = "Slot time is required")
    private String slotTime;

    private String dayOfWeek;
}