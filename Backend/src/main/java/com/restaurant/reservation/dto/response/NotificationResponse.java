package com.restaurant.reservation.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}