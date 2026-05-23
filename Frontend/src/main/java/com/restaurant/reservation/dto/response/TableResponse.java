package com.restaurant.reservation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableResponse {
    private Long id;
    private Integer tableNumber;
    private Integer capacity;
    private String location;
    private Boolean isAvailable;
}