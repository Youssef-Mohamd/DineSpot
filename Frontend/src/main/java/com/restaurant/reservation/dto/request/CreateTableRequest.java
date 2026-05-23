package com.restaurant.reservation.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    @NotNull(message = "Table number is required")
    @Min(value = 1)
    private Integer tableNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1)
    private Integer capacity;

    @NotBlank(message = "Location is required")
    private String location;  // indoor / outdoor
}