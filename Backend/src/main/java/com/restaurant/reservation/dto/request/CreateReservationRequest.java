package com.restaurant.reservation.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "Restaurant is required")
    private Long restaurantId;

    @NotBlank(message = "Date is required")
    private String reservationDate;

    @NotNull(message = "Time slot is required")
    private Long timeSlotId;

    @NotNull(message = "Guest count is required")
    @Min(value = 1)
    @Max(value = 20)
    private Integer guestCount;

    @Size(max = 500)
    private String specialRequest;
}