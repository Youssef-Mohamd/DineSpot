package com.restaurant.reservation.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private String restaurantName;
    private Integer tableNumber;
    private LocalDate reservationDate;
    private String slotTime;
    private Integer guestCount;
    private String status;
    private String specialRequest;
    private LocalDateTime createdAt;
}