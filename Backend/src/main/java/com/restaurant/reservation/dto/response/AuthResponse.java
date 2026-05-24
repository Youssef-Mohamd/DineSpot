package com.restaurant.reservation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String fullName;
    private String email;
    private String role;
}
