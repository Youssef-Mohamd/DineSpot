package com.restaurant.reservation.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableRequest {

    @Min(1)
    private Integer tableNumber;

    @Min(1)
    private Integer capacity;

    private String location;

    private Boolean isAvailable;
}
